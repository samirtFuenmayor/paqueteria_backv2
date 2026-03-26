package com.equalatam.equlatam_backv2.pedidos.service;

import com.equalatam.equlatam_backv2.cliente.entity.Cliente;
import com.equalatam.equlatam_backv2.cliente.entity.Parentesco;
import com.equalatam.equlatam_backv2.cliente.repositories.ClienteRepository;
import com.equalatam.equlatam_backv2.exception.ResourceNotFoundException;
import com.equalatam.equlatam_backv2.financiero.dto.CotizacionRequest;
import com.equalatam.equlatam_backv2.financiero.enums.EstadoCotizacion;
import com.equalatam.equlatam_backv2.financiero.repository.CotizacionRepository;
import com.equalatam.equlatam_backv2.financiero.repository.FacturaRepository;
import com.equalatam.equlatam_backv2.financiero.service.CotizacionService;
import com.equalatam.equlatam_backv2.pedidos.dto.request.PedidoItemRequest;
import com.equalatam.equlatam_backv2.pedidos.dto.request.PedidoRequest;
import com.equalatam.equlatam_backv2.pedidos.dto.response.PedidoResponse;
import com.equalatam.equlatam_backv2.pedidos.dto.response.PedidoResumenResponse;
import com.equalatam.equlatam_backv2.pedidos.entity.EstadoPedido;
import com.equalatam.equlatam_backv2.pedidos.entity.Pedido;
import com.equalatam.equlatam_backv2.pedidos.entity.PedidoItem;
import com.equalatam.equlatam_backv2.pedidos.entity.TipoPedido;
import com.equalatam.equlatam_backv2.pedidos.repository.PedidoItemRepository;
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
    // ─── Agrega esta dependencia en la clase ─────────────────────────────────
    private final CotizacionService cotizacionService;
    private final CotizacionRepository cotizacionRepository;
    private final FacturaRepository facturaRepository;
    private final PedidoItemRepository pedidoItemRepository;


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
        // ─── Categoría y titular ──────────────────────────────────────────────────────
        pedido.setCategoria(req.categoriaPedido());
        pedido.setEsPorTitular(Boolean.TRUE.equals(req.esPorTitular()));

        if (Boolean.TRUE.equals(req.esPorTitular()) && req.titularId() != null) {
            clienteRepository.findById(req.titularId())
                    .ifPresent(pedido::setTitular);
        }

        // ─── Items del pedido ─────────────────────────────────────────────────────────
        if (req.items() != null && !req.items().isEmpty()) {
            double pesoTotal = 0.0;
            for (PedidoItemRequest itemReq : req.items()) {
                PedidoItem item = new PedidoItem();
                item.setPedido(pedido);
                item.setTipoProducto(itemReq.tipoProducto());
                item.setDescripcion(itemReq.descripcion());
                item.setTrackingExterno(itemReq.trackingExterno());
                item.setProveedor(itemReq.proveedor());
                item.setPeso(itemReq.peso());
                item.setValorDeclarado(itemReq.valorDeclarado());
                item.setObservaciones(itemReq.observaciones());
                item.setLlego(false);
                item.setDespachado(false);
                pedido.getItems().add(item);
                if (itemReq.peso() != null) pesoTotal += itemReq.peso();
            }
            pedido.setPesoTotal(pesoTotal);
        }

        if (usernameEmpleado != null) {
            userRepository.findByUsername(usernameEmpleado)
                    .ifPresent(pedido::setRegistradoPor);
        }

        Pedido guardado = pedidoRepository.save(pedido);
       // notificacionService.notificarPedidoRegistrado(guardado);
        // ─── Auto-generar cotización si el cliente la solicitó ───────────────────
        if (Boolean.TRUE.equals(req.solicitaCotizacion())) {
            if (req.peso() == null || req.categoria() == null) {
                throw new IllegalArgumentException(
                        "Para solicitar cotización se requiere peso y categoría del paquete");
            }
            CotizacionRequest cotReq = new CotizacionRequest();
            cotReq.setClienteId(req.clienteId());
            cotReq.setPedidoId(guardado.getId());
            cotReq.setPesoReal(req.peso());
            cotReq.setLargo(req.largo());
            cotReq.setAncho(req.ancho());
            cotReq.setAlto(req.alto());
            cotReq.setValorDeclarado(req.valorDeclarado());
            cotReq.setCategoria(req.categoria());

            cotizacionService.crear(cotReq, null); // null = sistema, no un operador
        }

        // ─── Tarifa por parentesco ────────────────────────────────────────────────────
        if (Boolean.TRUE.equals(req.esPorTitular()) && req.titularId() != null) {
            Cliente afiliado = clienteRepository.findById(req.clienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

            if (afiliado.getTitular() == null ||
                    !afiliado.getTitular().getId().equals(req.titularId())) {
                throw new IllegalArgumentException(
                        "El cliente no está afiliado al titular indicado");
            }

            // Determinar tipo de tarifa según parentesco
            Parentesco parentesco = afiliado.getParentesco();
            String tipoTarifa = switch (parentesco) {
                case CONYUGE, HIJO, HIJA, PADRE, MADRE, HERMANO, HERMANA -> "FAMILIAR";
                case AMIGO, OTRO -> "AMIGO";
                case TITULAR -> "INDIVIDUAL";
            };
            pedido.setTipoTarifa(tipoTarifa);
        }

        // ── Registrar primer evento de tracking automáticamente ───────────────
        trackingService.registrarEvento(
                guardado, EstadoPedido.REGISTRADO,
                "Pedido registrado en el sistema",
                usernameEmpleado,
                origen.getId(), null, true, null
        );

        return PedidoResponse.from(guardado);

        //notificacionService.notificarPedidoRegistrado(guardado);
      //  notificacionService.notificarCambioEstado(guardado, nuevoEstado, observacion);
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
      //  notificacionService.notificarCambioEstado(guardado, nuevoEstado, observacion);

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
            case RECEPCION_PARCIAL  -> "Recepción parcial, pendiente decisión del cliente";
            case ESPERANDO_ITEMS    -> "Cliente esperando items faltantes en casillero";
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

    // ─── Resumen por cliente (logístico + financiero) ─────────────────────────
    public List<PedidoResumenResponse> resumenPorCliente(UUID clienteId) {
        return pedidoRepository.findByClienteId(clienteId)
                .stream()
                .map(pedido -> {
                    // Buscar cotización activa del pedido
                    var cotizaciones = cotizacionRepository.findByPedidoId(pedido.getId());
                    var cot = cotizaciones.isEmpty() ? null : cotizaciones.get(0);

                    // Buscar factura del pedido
                    var facturas = facturaRepository.findByPedidoId(pedido.getId());
                    var fac = facturas.isEmpty() ? null : facturas.get(0);

                    // Determinar estado financiero
                    String estadoFin;
                    if (fac != null) {
                        estadoFin = fac.getEstado().name(); // EMITIDA, PAGADA, etc.
                    } else if (cot != null) {
                        estadoFin = switch (cot.getEstado()) {
                            case PENDIENTE -> "COTIZADO";
                            case APROBADA  -> "PENDIENTE_PAGO";
                            default        -> cot.getEstado().name();
                        };
                    } else {
                        estadoFin = "SIN_COTIZAR";
                    }

                    return new PedidoResumenResponse(
                            pedido.getId(),
                            pedido.getNumeroPedido(),
                            pedido.getTipo(),
                            pedido.getDescripcion(),
                            pedido.getEstado(),
                            estadoFin,
                            cot != null ? cot.getId() : null,
                            cot != null ? cot.getTotal() : null,
                            fac != null ? fac.getId() : null,
                            fac != null ? fac.getNumeroFactura() : null,
                            fac != null ? fac.getTotal() : null,
                            pedido.getFechaRegistro()
                    );
                })
                .collect(Collectors.toList());
    }

    public List<PedidoResumenResponse> pedidosPendientesFacturar() {
        // Pedidos que tienen pago aprobado pero aún no tienen factura emitida
        return pedidoRepository.findAll()
                .stream()
                .map(pedido -> {
                    var facturas = facturaRepository.findByPedidoId(pedido.getId());
                    var fac = facturas.isEmpty() ? null : facturas.get(0);
                    var cotizaciones = cotizacionRepository.findByPedidoId(pedido.getId());
                    var cot = cotizaciones.isEmpty() ? null : cotizaciones.get(0);

                    // Solo incluir los que tienen cotización APROBADA y sin factura todavía
                    if (fac != null) return null;
                    if (cot == null) return null;
                    if (cot.getEstado() != EstadoCotizacion.APROBADA) return null;

                    return new PedidoResumenResponse(
                            pedido.getId(),
                            pedido.getNumeroPedido(),
                            pedido.getTipo(),
                            pedido.getDescripcion(),
                            pedido.getEstado(),
                            "LISTO_PARA_FACTURAR",
                            cot.getId(),
                            cot.getTotal(),
                            null, null, null,
                            pedido.getFechaRegistro()
                    );
                })
                .filter(r -> r != null)
                .collect(Collectors.toList());
    }

    // ─── Marcar item como llegado o no ───────────────────────────────────────────
    @Transactional
    public PedidoResponse marcarItemLlegado(UUID pedidoId, UUID itemId,
                                            Boolean llego, String observacion,
                                            String username) {
        Pedido pedido = getPedidoOrThrow(pedidoId);

        PedidoItem item = pedidoItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado: " + itemId));

        item.setLlego(llego);
        if (observacion != null) item.setObservaciones(observacion);
        pedidoItemRepository.save(item);

        return PedidoResponse.from(pedido);
    }

    // ─── Admin confirma recepción — revisa si llegaron todos ─────────────────────
    @Transactional
    public PedidoResponse confirmarRecepcion(UUID pedidoId, String username) {
        Pedido pedido = getPedidoOrThrow(pedidoId);

        List<PedidoItem> items = pedidoItemRepository.findByPedidoId(pedidoId);
        long llegaron  = items.stream().filter(i -> Boolean.TRUE.equals(i.getLlego())).count();
        long faltantes = items.stream().filter(i -> !Boolean.TRUE.equals(i.getLlego())).count();

        if (faltantes == 0) {
            // Todos llegaron — flujo normal
            pedido.setEstado(EstadoPedido.RECIBIDO_EN_SEDE);
            trackingService.registrarEvento(pedido, EstadoPedido.RECIBIDO_EN_SEDE,
                    "Todos los items recibidos en sede (" + llegaron + " items)",
                    username, pedido.getSucursalOrigen() != null
                            ? pedido.getSucursalOrigen().getId() : null, null, true, null);
        } else {
            // Faltan items — esperar decisión del cliente
            pedido.setEstado(EstadoPedido.RECEPCION_PARCIAL);
            trackingService.registrarEvento(pedido, EstadoPedido.RECEPCION_PARCIAL,
                    "Recepción parcial: llegaron " + llegaron + " de " + items.size()
                            + " items. Faltan " + faltantes + ".",
                    username, pedido.getSucursalOrigen() != null
                            ? pedido.getSucursalOrigen().getId() : null, null, true, null);
        }

        return PedidoResponse.from(pedidoRepository.save(pedido));
    }

    // ─── Cliente decide despachar parcial o esperar ───────────────────────────────
    @Transactional
    public PedidoResponse decisionDespacho(UUID pedidoId, boolean despacharParcial) {
        Pedido pedido = getPedidoOrThrow(pedidoId);

        if (pedido.getEstado() != EstadoPedido.RECEPCION_PARCIAL) {
            throw new IllegalArgumentException(
                    "El pedido no está en estado RECEPCION_PARCIAL");
        }

        if (despacharParcial) {
            // Despachar solo lo que llegó
            pedido.setEstado(EstadoPedido.RECIBIDO_EN_SEDE);
            trackingService.registrarEvento(pedido, EstadoPedido.RECIBIDO_EN_SEDE,
                    "Cliente aprobó despacho parcial de los items recibidos.",
                    null, null, null, true, null);
        } else {
            // Esperar los items faltantes
            pedido.setEstado(EstadoPedido.ESPERANDO_ITEMS);
            trackingService.registrarEvento(pedido, EstadoPedido.ESPERANDO_ITEMS,
                    "Cliente decidió esperar los items faltantes en casillero.",
                    null, null, null, true, null);
        }

        return PedidoResponse.from(pedidoRepository.save(pedido));
    }
}