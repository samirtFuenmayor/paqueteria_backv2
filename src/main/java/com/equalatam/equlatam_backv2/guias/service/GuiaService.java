package com.equalatam.equlatam_backv2.guias.service;

import com.equalatam.equlatam_backv2.cliente.repositories.ClienteRepository;
import com.equalatam.equlatam_backv2.exception.ResourceNotFoundException;
import com.equalatam.equlatam_backv2.guias.dto.request.GuiaRequest;
import com.equalatam.equlatam_backv2.guias.dto.response.GuiaResponse;
import com.equalatam.equlatam_backv2.guias.entity.EstadoGuia;
import com.equalatam.equlatam_backv2.guias.entity.Guia;
import com.equalatam.equlatam_backv2.guias.repository.GuiaRepository;
import com.equalatam.equlatam_backv2.pedidos.entity.Pedido;
import com.equalatam.equlatam_backv2.pedidos.repository.PedidoRepository;
import com.equalatam.equlatam_backv2.repository.UserRepository;
import com.equalatam.equlatam_backv2.sucursales.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuiaService {

    private final GuiaRepository guiaRepository;
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final SucursalRepository sucursalRepository;
    private final UserRepository userRepository;

    // Tarifas base (configurables más adelante en módulo de tarifas)
    private static final double TARIFA_POR_LIBRA_USA    = 3.50;
    private static final double TARIFA_POR_LIBRA_CANADA = 4.00;
    private static final double COSTO_MANEJO            = 5.00;
    private static final double PORCENTAJE_SEGURO       = 0.02; // 2% del valor declarado
    private static final double DIVISOR_VOLUMETRICO     = 166.0; // estándar aéreo

    // ─── Generar guía desde un pedido ─────────────────────────────────────────
    @Transactional
    public GuiaResponse generar(GuiaRequest req, String username) {

        Pedido pedido = pedidoRepository.findById(req.pedidoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pedido no encontrado: " + req.pedidoId()));

        if (guiaRepository.existsByPedidoId(req.pedidoId())) {
            throw new IllegalArgumentException(
                    "El pedido " + pedido.getNumeroPedido() + " ya tiene una guía generada");
        }

        Guia g = new Guia();
        g.setNumeroGuia(generarNumeroGuia());
        g.setPedido(pedido);
        g.setDestinatario(pedido.getCliente());

        // Remitente
        g.setRemitenteNombre(req.remitenteNombre());
        g.setRemitenteDireccion(req.remitenteDireccion());
        g.setRemitenteTelefono(req.remitenteTelefono());
        g.setRemitenteEmail(req.remitenteEmail());
        g.setRemitentePais(req.remitentePais());

        // Sucursales desde el pedido
        g.setSucursalOrigen(pedido.getSucursalOrigen());
        g.setSucursalDestino(pedido.getSucursalDestino());

        // Contenido
        g.setDescripcionContenido(req.descripcionContenido());
        g.setValorDeclarado(req.valorDeclarado());
        g.setCantidadPiezas(req.cantidadPiezas());
        g.setLargo(req.largo());
        g.setAncho(req.ancho());
        g.setAlto(req.alto());

        // ── Calcular pesos ────────────────────────────────────────────────────
        double pesoReal = req.pesoDeclarado() != null ? req.pesoDeclarado() : 0;
        g.setPesoDeclarado(pesoReal);

        // Peso volumétrico = (L x A x H) / 166
        double pesoVol = 0;
        if (req.largo() != null && req.ancho() != null && req.alto() != null) {
            pesoVol = (req.largo() * req.ancho() * req.alto()) / DIVISOR_VOLUMETRICO;
        }
        g.setPesoVolumetrico(Math.round(pesoVol * 100.0) / 100.0);

        // Peso cobrable = el mayor entre real y volumétrico
        double pesoCobrable = Math.max(pesoReal, pesoVol);
        g.setPesoCobrable(Math.round(pesoCobrable * 100.0) / 100.0);

        // ── Calcular costos ───────────────────────────────────────────────────
        double tarifa = obtenerTarifa(pedido);
        g.setTarifaPorLibra(tarifa);

        double costoFlete = pesoCobrable * tarifa;
        g.setCostoFlete(Math.round(costoFlete * 100.0) / 100.0);
        g.setCostoManejo(COSTO_MANEJO);

        double costoSeguro = req.valorDeclarado() != null ?
                req.valorDeclarado() * PORCENTAJE_SEGURO : 0;
        g.setCostoSeguro(Math.round(costoSeguro * 100.0) / 100.0);

        double total = costoFlete + COSTO_MANEJO + costoSeguro;
        g.setCostoTotal(Math.round(total * 100.0) / 100.0);

        // Transporte (si ya viene asignado al despacho)
        g.setNumeroDespacho(req.numeroDespacho());
        g.setAerolinea(req.aerolinea());
        g.setNumeroVuelo(req.numeroVuelo());
        g.setGuiaAerea(req.guiaAerea());
        g.setObservaciones(req.observaciones());

        if (req.numeroDespacho() != null) {
            g.setEstado(EstadoGuia.ASIGNADA);
        }

        if (username != null) {
            userRepository.findByUsername(username).ifPresent(g::setGeneradaPor);
        }

        return GuiaResponse.from(guiaRepository.save(g));
    }

    // ─── Asignar guía a un despacho ───────────────────────────────────────────
    @Transactional
    public GuiaResponse asignarDespacho(UUID guiaId, String numeroDespacho,
                                        String aerolinea, String vuelo, String awb) {
        Guia g = getGuiaOrThrow(guiaId);
        g.setNumeroDespacho(numeroDespacho);
        g.setAerolinea(aerolinea);
        g.setNumeroVuelo(vuelo);
        g.setGuiaAerea(awb);
        g.setEstado(EstadoGuia.ASIGNADA);
        return GuiaResponse.from(guiaRepository.save(g));
    }

    // ─── Cambiar estado ───────────────────────────────────────────────────────
    @Transactional
    public GuiaResponse cambiarEstado(UUID id, EstadoGuia nuevoEstado) {
        Guia g = getGuiaOrThrow(id);
        g.setEstado(nuevoEstado);
        if (nuevoEstado == EstadoGuia.ENTREGADA) {
            g.setFechaEntrega(LocalDateTime.now());
        }
        return GuiaResponse.from(guiaRepository.save(g));
    }

    // ─── Anular guía ──────────────────────────────────────────────────────────
    @Transactional
    public GuiaResponse anular(UUID id, String motivo) {
        Guia g = getGuiaOrThrow(id);
        if (g.getEstado() == EstadoGuia.ENTREGADA) {
            throw new IllegalArgumentException("No se puede anular una guía ya entregada");
        }
        g.setEstado(EstadoGuia.ANULADA);
        g.setObservaciones("ANULADA: " + motivo);
        return GuiaResponse.from(guiaRepository.save(g));
    }

    // ─── Consultas ────────────────────────────────────────────────────────────
    public List<GuiaResponse> findAll() {
        return guiaRepository.findAll().stream()
                .map(GuiaResponse::from).collect(Collectors.toList());
    }

    public GuiaResponse findById(UUID id) {
        return GuiaResponse.from(getGuiaOrThrow(id));
    }

    public GuiaResponse findByNumero(String numero) {
        return GuiaResponse.from(
                guiaRepository.findByNumeroGuia(numero)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Guía no encontrada: " + numero))
        );
    }

    public GuiaResponse findByPedido(UUID pedidoId) {
        return GuiaResponse.from(
                guiaRepository.findByPedidoId(pedidoId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "No existe guía para el pedido: " + pedidoId))
        );
    }

    public List<GuiaResponse> findByEstado(EstadoGuia estado) {
        return guiaRepository.findByEstado(estado).stream()
                .map(GuiaResponse::from).collect(Collectors.toList());
    }

    public List<GuiaResponse> findByCliente(UUID clienteId) {
        return guiaRepository.findByDestinatarioId(clienteId).stream()
                .map(GuiaResponse::from).collect(Collectors.toList());
    }

    public List<GuiaResponse> findByDespacho(String numeroDespacho) {
        return guiaRepository.findByNumeroDespacho(numeroDespacho).stream()
                .map(GuiaResponse::from).collect(Collectors.toList());
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private double obtenerTarifa(Pedido pedido) {
        if (pedido.getSucursalOrigen() == null) return TARIFA_POR_LIBRA_USA;
        String pais = pedido.getSucursalOrigen().getPais().toLowerCase();
        if (pais.contains("canad")) return TARIFA_POR_LIBRA_CANADA;
        return TARIFA_POR_LIBRA_USA;
    }

    private String generarNumeroGuia() {
        String anio = String.valueOf(Year.now().getValue());
        long total = guiaRepository.count() + 1;
        String numero = String.format("GUI-%s-%05d", anio, total);
        while (guiaRepository.existsByNumeroGuia(numero)) {
            total++;
            numero = String.format("GUI-%s-%05d", anio, total);
        }
        return numero;
    }

    private Guia getGuiaOrThrow(UUID id) {
        return guiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Guía no encontrada: " + id));
    }
}
