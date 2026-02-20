package com.equalatam.equlatam_backv2.pedidos.service;

import com.equalatam.equlatam_backv2.cliente.entity.Cliente;
import com.equalatam.equlatam_backv2.cliente.repositories.ClienteRepository;
import com.equalatam.equlatam_backv2.exception.ResourceNotFoundException;
import com.equalatam.equlatam_backv2.pedidos.dto.request.PedidoRequest;
import com.equalatam.equlatam_backv2.pedidos.dto.response.PedidoResponse;
import com.equalatam.equlatam_backv2.pedidos.entity.EstadoPedido;
import com.equalatam.equlatam_backv2.pedidos.entity.Pedido;
import com.equalatam.equlatam_backv2.pedidos.entity.TipoPedido;
import com.equalatam.equlatam_backv2.pedidos.repository.PedidoRepository;
import com.equalatam.equlatam_backv2.repository.UserRepository;
import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import com.equalatam.equlatam_backv2.sucursales.repository.SucursalRepository;
import com.equalatam.equlatam_backv2.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final SucursalRepository sucursalRepository;
    private final UserRepository userRepository;
    private final TrackingService trackingService; // ← NUEVO

    // ─── Crear pedido ─────────────────────────────────────────────────────────
    @Transactional
    public PedidoResponse create(PedidoRequest req, String usernameEmpleado) {

        if (req.trackingExterno() != null &&
                pedidoRepository.existsByTrackingExterno(req.trackingExterno())) {
            throw new IllegalArgumentException(
                    "Ya existe un pedido con el tracking: " + req.trackingExterno());
        }

        Cliente cliente = clienteRepository.findById(req.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado: " + req.clienteId()));

        Sucursal origen = sucursalRepository.findById(req.sucursalOrigenId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sucursal origen no encontrada: " + req.sucursalOrigenId()));

        Sucursal destino = sucursalRepository.findById(req.sucursalDestinoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sucursal destino no encontrada: " + req.sucursalDestinoId()));

        Pedido pedido = new Pedido();
        pedido.setNumeroPedido(generarNumeroPedido());
        pedido.setTipo(req.tipo());
        pedido.setCliente(cliente);
        pedido.setTrackingExterno(req.trackingExterno());
        pedido.setProveedor(req.proveedor());
        pedido.setUrlTracking(req.urlTracking());
        pedido.setDescripcion(req.descripcion());
        pedido.setPeso(req.peso());
        pedido.setLargo(req.largo());
        pedido.setAncho(req.ancho());
        pedido.setAlto(req.alto());
        pedido.setValorDeclarado(req.valorDeclarado());
        pedido.setCantidadItems(req.cantidadItems());
        pedido.setSucursalOrigen(origen);
        pedido.setSucursalDestino(destino);
        pedido.setObservaciones(req.observaciones());
        pedido.setNotasInternas(req.notasInternas());
        pedido.setFotoUrl(req.fotoUrl());

        if (usernameEmpleado != null) {
            userRepository.findByUsername(usernameEmpleado)
                    .ifPresent(pedido::setRegistradoPor);
        }

        Pedido guardado = pedidoRepository.save(pedido);

        // ── Registrar primer evento de tracking automáticamente ───────────────
        trackingService.registrarEvento(
                guardado, EstadoPedido.REGISTRADO,
                "Pedido registrado en el sistema",
                usernameEmpleado,
                origen.getId(), null, true, null
        );

        return PedidoResponse.from(guardado);
    }

    // ─── Listar todos ─────────────────────────────────────────────────────────
    public List<PedidoResponse> findAll() {
        return pedidoRepository.findAll()
                .stream().map(PedidoResponse::from).collect(Collectors.toList());
    }

    // ─── Buscar por ID ────────────────────────────────────────────────────────
    public PedidoResponse findById(UUID id) {
        return PedidoResponse.from(getPedidoOrThrow(id));
    }

    // ─── Buscar por número ────────────────────────────────────────────────────
    public PedidoResponse findByNumeroPedido(String numeroPedido) {
        return PedidoResponse.from(
                pedidoRepository.findByNumeroPedido(numeroPedido)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Pedido no encontrado: " + numeroPedido))
        );
    }

    // ─── Por cliente ──────────────────────────────────────────────────────────
    public List<PedidoResponse> findByCliente(UUID clienteId) {
        return pedidoRepository.findByClienteId(clienteId)
                .stream().map(PedidoResponse::from).collect(Collectors.toList());
    }

    // ─── Por estado ───────────────────────────────────────────────────────────
    public List<PedidoResponse> findByEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado)
                .stream().map(PedidoResponse::from).collect(Collectors.toList());
    }

    // ─── Por sucursal origen ──────────────────────────────────────────────────
    public List<PedidoResponse> findBySucursalOrigen(UUID sucursalId) {
        return pedidoRepository.findBySucursalOrigenId(sucursalId)
                .stream().map(PedidoResponse::from).collect(Collectors.toList());
    }

    // ─── Por sucursal destino ─────────────────────────────────────────────────
    public List<PedidoResponse> findBySucursalDestino(UUID sucursalId) {
        return pedidoRepository.findBySucursalDestinoId(sucursalId)
                .stream().map(PedidoResponse::from).collect(Collectors.toList());
    }

    // ─── Listos para despachar ────────────────────────────────────────────────
    public List<PedidoResponse> findListosParaDespachar(UUID sucursalOrigenId) {
        return pedidoRepository.findListosParaDespachar(sucursalOrigenId)
                .stream().map(PedidoResponse::from).collect(Collectors.toList());
    }

    // ─── Disponibles para retiro ──────────────────────────────────────────────
    public List<PedidoResponse> findDisponiblesEnSucursal(UUID sucursalDestinoId) {
        return pedidoRepository.findDisponiblesEnSucursal(sucursalDestinoId)
                .stream().map(PedidoResponse::from).collect(Collectors.toList());
    }

    // ─── Buscador ─────────────────────────────────────────────────────────────
    public List<PedidoResponse> buscar(String query) {
        return pedidoRepository.buscar(query)
                .stream().map(PedidoResponse::from).collect(Collectors.toList());
    }

    // ─── Dashboard ────────────────────────────────────────────────────────────
    public Map<String, Long> conteosPorEstado() {
        return Map.of(
                "REGISTRADO",               pedidoRepository.countByEstado(EstadoPedido.REGISTRADO),
                "RECIBIDO_EN_SEDE",         pedidoRepository.countByEstado(EstadoPedido.RECIBIDO_EN_SEDE),
                "EN_TRANSITO",              pedidoRepository.countByEstado(EstadoPedido.EN_TRANSITO),
                "EN_ADUANA",                pedidoRepository.countByEstado(EstadoPedido.EN_ADUANA),
                "DISPONIBLE_EN_SUCURSAL",   pedidoRepository.countByEstado(EstadoPedido.DISPONIBLE_EN_SUCURSAL),
                "ENTREGADO",                pedidoRepository.countByEstado(EstadoPedido.ENTREGADO)
        );
    }

    // ─── Actualizar ───────────────────────────────────────────────────────────
    @Transactional
    public PedidoResponse update(UUID id, PedidoRequest req) {
        Pedido pedido = getPedidoOrThrow(id);

        Cliente cliente = clienteRepository.findById(req.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        Sucursal origen = sucursalRepository.findById(req.sucursalOrigenId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal origen no encontrada"));
        Sucursal destino = sucursalRepository.findById(req.sucursalDestinoId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal destino no encontrada"));

        pedido.setCliente(cliente);
        pedido.setTipo(req.tipo());
        pedido.setTrackingExterno(req.trackingExterno());
        pedido.setProveedor(req.proveedor());
        pedido.setUrlTracking(req.urlTracking());
        pedido.setDescripcion(req.descripcion());
        pedido.setPeso(req.peso());
        pedido.setLargo(req.largo());
        pedido.setAncho(req.ancho());
        pedido.setAlto(req.alto());
        pedido.setValorDeclarado(req.valorDeclarado());
        pedido.setCantidadItems(req.cantidadItems());
        pedido.setSucursalOrigen(origen);
        pedido.setSucursalDestino(destino);
        pedido.setObservaciones(req.observaciones());
        pedido.setNotasInternas(req.notasInternas());
        pedido.setFotoUrl(req.fotoUrl());

        return PedidoResponse.from(pedidoRepository.save(pedido));
    }

    // ─── Cambiar estado CON tracking automático ───────────────────────────────
    @Transactional
    public PedidoResponse cambiarEstado(UUID id, EstadoPedido nuevoEstado,
                                        String observacion, String username,
                                        UUID sucursalId) {
        Pedido pedido = getPedidoOrThrow(id);
        LocalDateTime ahora = LocalDateTime.now();

        switch (nuevoEstado) {
            case RECIBIDO_EN_SEDE       -> pedido.setFechaRecepcionSede(ahora);
            case EN_TRANSITO            -> pedido.setFechaSalidaExterior(ahora);
            case RECIBIDO_EN_MATRIZ     -> pedido.setFechaLlegadaEcuador(ahora);
            case DISPONIBLE_EN_SUCURSAL -> pedido.setFechaDisponible(ahora);
            case ENTREGADO              -> pedido.setFechaEntrega(ahora);
            default                     -> {}
        }

        pedido.setEstado(nuevoEstado);
        if (observacion != null && !observacion.isBlank()) {
            pedido.setObservaciones(observacion);
        }

        Pedido guardado = pedidoRepository.save(pedido);

        // ── Registrar evento de tracking automáticamente ──────────────────────
        String desc = observacion != null && !observacion.isBlank()
                ? observacion : obtenerDescripcionEstado(nuevoEstado);

        trackingService.registrarEvento(
                guardado, nuevoEstado, desc,
                username, sucursalId, null, true, null
        );

        return PedidoResponse.from(guardado);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private String obtenerDescripcionEstado(EstadoPedido estado) {
        return switch (estado) {
            case REGISTRADO             -> "Pedido registrado en el sistema";
            case RECIBIDO_EN_SEDE       -> "Paquete recibido en sede exterior";
            case EN_CONSOLIDACION       -> "Paquete agrupado para despacho";
            case EN_TRANSITO            -> "Paquete en tránsito hacia Ecuador";
            case EN_ADUANA              -> "Paquete retenido en aduana";
            case RETENIDO_ADUANA        -> "Paquete retenido, requiere documentación";
            case LIBERADO_ADUANA        -> "Paquete liberado de aduana";
            case RECIBIDO_EN_MATRIZ     -> "Paquete llegó a sede Quito";
            case EN_DISTRIBUCION        -> "Paquete en camino a sucursal destino";
            case DISPONIBLE_EN_SUCURSAL -> "Paquete disponible para retiro en sucursal";
            case ENTREGADO              -> "Paquete entregado al cliente";
            case DEVUELTO               -> "Paquete devuelto al remitente";
            case EXTRAVIADO             -> "Paquete reportado como extraviado";
        };
    }

    private String generarNumeroPedido() {
        String anio = String.valueOf(Year.now().getValue());
        long total = pedidoRepository.count() + 1;
        String numero = String.format("PED-%s-%05d", anio, total);
        while (pedidoRepository.existsByNumeroPedido(numero)) {
            total++;
            numero = String.format("PED-%s-%05d", anio, total);
        }
        return numero;
    }

    private Pedido getPedidoOrThrow(UUID id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pedido no encontrado: " + id));
    }
}