package com.equalatam.equlatam_backv2.financiero.service;

import com.equalatam.equlatam_backv2.cliente.repositories.ClienteRepository;
import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.financiero.dto.AprobarCotizacionRequest;
import com.equalatam.equlatam_backv2.financiero.dto.CotizacionRequest;
import com.equalatam.equlatam_backv2.financiero.dto.CotizacionResponse;
import com.equalatam.equlatam_backv2.financiero.entity.Cotizacion;
import com.equalatam.equlatam_backv2.financiero.entity.Tarifa;
import com.equalatam.equlatam_backv2.financiero.enums.EstadoCotizacion;
import com.equalatam.equlatam_backv2.financiero.repository.CotizacionRepository;
import com.equalatam.equlatam_backv2.financiero.repository.TarifaRepository;
import com.equalatam.equlatam_backv2.pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CotizacionService {

    private final CotizacionRepository repo;
    private final ClienteRepository    clienteRepo;
    private final PedidoRepository     pedidoRepo;
    private final TarifaRepository     tarifaRepo;
    private final TarifaService        tarifaService;
    private final NumeroSecuencialService secuencialService;


    // ─── Crear cotización ─────────────────────────────────────────────────────

    @Transactional
    public Cotizacion crear(CotizacionRequest req, User operador) {

        Cotizacion c = new Cotizacion();

        c.setNumeroCotizacion(secuencialService.siguienteNumeroCotizacion());

        c.setCliente(clienteRepo.findById(req.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado")));

        if (req.getPedidoId() != null) {
            c.setPedido(pedidoRepo.findById(req.getPedidoId())
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado")));
        }

        // Resolver tarifa: manual o automática
        Tarifa tarifa;
        if (req.getTarifaId() != null) {
            tarifa = tarifaRepo.findById(req.getTarifaId())
                    .orElseThrow(() -> new RuntimeException("Tarifa no encontrada"));
        } else {
            // Buscar automáticamente por categoría + tipo + peso
            if (req.getCategoria() == null) {
                throw new RuntimeException("Se requiere categoría o tarifaId para cotizar");
            }
            // Tipo de pedido: tomarlo del pedido si existe, o IMPORTACION por defecto
            var tipoPedido = (c.getPedido() != null)
                    ? c.getPedido().getTipo()
                    : com.equalatam.equlatam_backv2.pedidos.entity.TipoPedido.IMPORTACION;

            tarifa = tarifaService
                    .buscarTarifaAplicable(req.getCategoria(), tipoPedido,
                            req.getPesoReal() != null ? req.getPesoReal() : 0.0)
                    .orElseThrow(() -> new RuntimeException(
                            "No existe tarifa activa para la categoría y tipo indicados"));
        }
        c.setTarifa(tarifa);

        // Datos del paquete
        c.setPesoReal(req.getPesoReal());
        c.setLargo(req.getLargo());
        c.setAncho(req.getAncho());
        c.setAlto(req.getAlto());
        c.setValorDeclarado(req.getValorDeclarado());

        // Calcular
        TarifaService.ResultadoCalculo calc = tarifaService.calcular(
                tarifa,
                req.getPesoReal(),
                req.getLargo(), req.getAncho(), req.getAlto(),
                req.getValorDeclarado());

        c.setPesoVolumetrico(calc.pesoVolumetrico());
        c.setPesoFacturable(calc.pesoFacturable());
        c.setSubtotal(calc.subtotal());
        c.setPorcentajeIva(tarifa.getPorcentajeIva() != null ? tarifa.getPorcentajeIva() : 15.0);
        c.setMontoIva(calc.iva());
        c.setTotal(calc.total());
        c.setDetalleCalculo(calc.desglose());

        c.setEstado(EstadoCotizacion.PENDIENTE);
        c.setValidaHasta(LocalDate.now().plusDays(7));
        c.setObservaciones(req.getObservaciones());
        c.setCreadoPor(operador);

        return repo.save(c);
    }

    // ─── Aprobar cotización ───────────────────────────────────────────────────

    @Transactional
    public Cotizacion aprobar(UUID id) {
        Cotizacion c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));
        if (c.getEstado() != EstadoCotizacion.PENDIENTE) {
            throw new RuntimeException("Solo se pueden aprobar cotizaciones en estado PENDIENTE");
        }
        if (LocalDate.now().isAfter(c.getValidaHasta())) {
            c.setEstado(EstadoCotizacion.VENCIDA);
            repo.save(c);
            throw new RuntimeException("La cotización ha vencido");
        }
        c.setEstado(EstadoCotizacion.APROBADA);
        return repo.save(c);
    }

    // ─── Cancelar cotización ──────────────────────────────────────────────────

    @Transactional
    public Cotizacion cancelar(UUID id) {
        Cotizacion c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));
        if (c.getEstado() == EstadoCotizacion.FACTURADA) {
            throw new RuntimeException("No se puede cancelar una cotización ya facturada");
        }
        c.setEstado(EstadoCotizacion.CANCELADA);
        return repo.save(c);
    }

    // ─── Consultas ────────────────────────────────────────────────────────────

    public List<Cotizacion> listarPorCliente(UUID clienteId) {
        return repo.findByClienteIdOrderByCreadoEnDesc(clienteId);
    }

    public List<Cotizacion> listarPorPedido(UUID pedidoId) {
        return repo.findByPedidoId(pedidoId);
    }

    public Cotizacion obtener(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));
    }

    public List<Cotizacion> listarPendientes() {
        // Traer PENDIENTE y APROBADA para que el cajero pueda facturarlas
        return repo.findByEstadoIn(
                List.of(EstadoCotizacion.PENDIENTE, EstadoCotizacion.APROBADA));
    }
    // ─── Vencer cotizaciones expiradas (para scheduler) ──────────────────────

    @Transactional
    public void vencerExpiradas() {
        List<Cotizacion> pendientes = repo.findByEstado(EstadoCotizacion.PENDIENTE);
        LocalDate hoy = LocalDate.now();
        pendientes.stream()
                .filter(c -> hoy.isAfter(c.getValidaHasta()))
                .forEach(c -> {
                    c.setEstado(EstadoCotizacion.VENCIDA);
                    repo.save(c);
                });
    }

    @Transactional
    public Cotizacion aprobarPorCliente(UUID id, AprobarCotizacionRequest req) {
        Cotizacion c = obtener(id);

        if (c.getEstado() != EstadoCotizacion.PENDIENTE) {
            throw new RuntimeException("La cotización no está en estado PENDIENTE");
        }
        if (LocalDate.now().isAfter(c.getValidaHasta())) {
            c.setEstado(EstadoCotizacion.VENCIDA);
            repo.save(c);
            throw new RuntimeException("La cotización ha vencido");
        }

        // Aprobar
        c.setEstado(EstadoCotizacion.APROBADA);

        // Guardar la forma de pago elegida por el cliente en las observaciones
        // hasta que el admin cree el Pago formal
        String infoPago = String.format("[PAGO CLIENTE] Forma: %s | Ref: %s | %s",
                req.formaPago(),
                req.referenciaPago() != null ? req.referenciaPago() : "N/A",
                req.observaciones() != null ? req.observaciones() : "");
        c.setObservaciones(infoPago);

        return repo.save(c);
    }


    public CotizacionResponse toResponse(Cotizacion c) {
        CotizacionResponse r = new CotizacionResponse();
        r.setId(c.getId());
        r.setNumeroCotizacion(c.getNumeroCotizacion());
        r.setEstado(c.getEstado() != null ? c.getEstado().name() : null);
        r.setSubtotal(c.getSubtotal());
        r.setPorcentajeIva(c.getPorcentajeIva());
        r.setMontoIva(c.getMontoIva());
        r.setTotal(c.getTotal());
        r.setPesoReal(c.getPesoReal());
        r.setPesoVolumetrico(c.getPesoVolumetrico());
        r.setPesoFacturable(c.getPesoFacturable());
        r.setValorDeclarado(c.getValorDeclarado());
        r.setValidaHasta(c.getValidaHasta());
        r.setObservaciones(c.getObservaciones());
        r.setDetalleCalculo(c.getDetalleCalculo());
        r.setCreadoEn(c.getCreadoEn() != null ? c.getCreadoEn().toString() : null);
        if (c.getTarifa() != null) r.setCategoria(c.getTarifa().getCategoria());
        if (c.getCliente() != null) {
            r.setClienteNombre(c.getCliente().getNombres() + " " + c.getCliente().getApellidos());
            r.setClienteIdentificacion(c.getCliente().getNumeroIdentificacion());
        }
        if (c.getPedido() != null) r.setPedidoNumero(c.getPedido().getNumeroPedido());
        if (c.getTarifa() != null) r.setTarifaNombre(c.getTarifa().getNombre());
        return r;
    }
}