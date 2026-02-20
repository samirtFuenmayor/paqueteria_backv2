package com.equalatam.equlatam_backv2.despachos.service;


import com.equalatam.equlatam_backv2.despachos.dto.request.DespachoRequest;
import com.equalatam.equlatam_backv2.despachos.dto.response.DespachoResponse;
import com.equalatam.equlatam_backv2.despachos.entity.Despacho;
import com.equalatam.equlatam_backv2.despachos.entity.DespachoDetalle;
import com.equalatam.equlatam_backv2.despachos.entity.EstadoDespacho;
import com.equalatam.equlatam_backv2.despachos.repository.DespachoDetalleRepository;
import com.equalatam.equlatam_backv2.despachos.repository.DespachoRepository;
import com.equalatam.equlatam_backv2.exception.ResourceNotFoundException;
import com.equalatam.equlatam_backv2.pedidos.entity.EstadoPedido;
import com.equalatam.equlatam_backv2.pedidos.entity.Pedido;
import com.equalatam.equlatam_backv2.pedidos.repository.PedidoRepository;
import com.equalatam.equlatam_backv2.repository.UserRepository;
import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import com.equalatam.equlatam_backv2.sucursales.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DespachoService {

    private final DespachoRepository despachoRepository;
    private final DespachoDetalleRepository detalleRepository;
    private final PedidoRepository pedidoRepository;
    private final SucursalRepository sucursalRepository;
    private final UserRepository userRepository;

    // ─── Crear despacho ───────────────────────────────────────────────────────
    @Transactional
    public DespachoResponse create(DespachoRequest req, String usernameEmpleado) {

        Sucursal origen = sucursalRepository.findById(req.sucursalOrigenId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sucursal origen no encontrada: " + req.sucursalOrigenId()));

        Sucursal destino = sucursalRepository.findById(req.sucursalDestinoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sucursal destino no encontrada: " + req.sucursalDestinoId()));

        if (origen.getId().equals(destino.getId())) {
            throw new IllegalArgumentException(
                    "La sucursal origen y destino no pueden ser la misma");
        }

        Despacho d = new Despacho();
        d.setNumeroDespacho(generarNumeroDespacho());
        d.setSucursalOrigen(origen);
        d.setSucursalDestino(destino);
        d.setAerolinea(req.aerolinea());
        d.setNumeroVuelo(req.numeroVuelo());
        d.setGuiaAerea(req.guiaAerea());
        d.setNumeroContenedor(req.numeroContenedor());
        d.setTipoTransporte(req.tipoTransporte());
        d.setFechaSalidaProgramada(req.fechaSalidaProgramada());
        d.setFechaLlegadaProgramada(req.fechaLlegadaProgramada());
        d.setObservaciones(req.observaciones());

        if (usernameEmpleado != null) {
            userRepository.findByUsername(usernameEmpleado).ifPresent(d::setCreadoPor);
        }

        return DespachoResponse.from(despachoRepository.save(d));
    }

    // ─── Agregar pedidos al despacho ──────────────────────────────────────────
    @Transactional
    public DespachoResponse agregarPedidos(UUID despachoId, Set<UUID> pedidoIds) {
        Despacho despacho = getDespachoOrThrow(despachoId);

        if (despacho.getEstado() != EstadoDespacho.ABIERTO) {
            throw new IllegalArgumentException(
                    "Solo se pueden agregar pedidos a despachos en estado ABIERTO. " +
                            "Estado actual: " + despacho.getEstado());
        }

        for (UUID pedidoId : pedidoIds) {
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Pedido no encontrado: " + pedidoId));

            // Validar que el pedido esté en estado correcto
            if (pedido.getEstado() != EstadoPedido.RECIBIDO_EN_SEDE) {
                throw new IllegalArgumentException(
                        "El pedido " + pedido.getNumeroPedido() +
                                " debe estar en estado RECIBIDO_EN_SEDE para ser despachado. " +
                                "Estado actual: " + pedido.getEstado());
            }

            // Validar que no esté ya en otro despacho activo
            if (despachoRepository.pedidoYaEnDespachoActivo(pedidoId)) {
                throw new IllegalArgumentException(
                        "El pedido " + pedido.getNumeroPedido() + " ya está en otro despacho activo");
            }

            // Validar que no esté ya en este despacho
            if (detalleRepository.existsByDespachoIdAndPedidoId(despachoId, pedidoId)) {
                throw new IllegalArgumentException(
                        "El pedido " + pedido.getNumeroPedido() + " ya está en este despacho");
            }

            // Agregar al despacho
            DespachoDetalle detalle = new DespachoDetalle();
            detalle.setDespacho(despacho);
            detalle.setPedido(pedido);
            detalleRepository.save(detalle);

            // Cambiar estado del pedido
            pedido.setEstado(EstadoPedido.EN_CONSOLIDACION);
            pedidoRepository.save(pedido);
        }

        // Recalcular totales
        recalcularTotales(despacho);
        return DespachoResponse.from(despachoRepository.save(despacho));
    }

    // ─── Quitar pedido del despacho ───────────────────────────────────────────
    @Transactional
    public DespachoResponse quitarPedido(UUID despachoId, UUID pedidoId) {
        Despacho despacho = getDespachoOrThrow(despachoId);

        if (despacho.getEstado() != EstadoDespacho.ABIERTO) {
            throw new IllegalArgumentException(
                    "Solo se pueden quitar pedidos de despachos en estado ABIERTO");
        }

        DespachoDetalle detalle = detalleRepository
                .findByDespachoIdAndPedidoId(despachoId, pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El pedido no está en este despacho"));

        // Revertir estado del pedido
        Pedido pedido = detalle.getPedido();
        pedido.setEstado(EstadoPedido.RECIBIDO_EN_SEDE);
        pedidoRepository.save(pedido);

        detalleRepository.delete(detalle);

        recalcularTotales(despacho);
        return DespachoResponse.from(despachoRepository.save(despacho));
    }

    // ─── Cambiar estado del despacho ──────────────────────────────────────────
    @Transactional
    public DespachoResponse cambiarEstado(UUID id, EstadoDespacho nuevoEstado, String observacion) {
        Despacho despacho = getDespachoOrThrow(id);
        LocalDateTime ahora = LocalDateTime.now();

        // Validar transiciones de estado
        validarTransicion(despacho.getEstado(), nuevoEstado);

        // Actualizar fechas y estados de pedidos según el nuevo estado
        switch (nuevoEstado) {
            case CERRADO -> {
                if (despacho.getDetalles().isEmpty()) {
                    throw new IllegalArgumentException(
                            "No se puede cerrar un despacho sin pedidos");
                }
            }
            case EN_TRANSITO -> {
                despacho.setFechaSalidaReal(ahora);
                // Actualizar todos los pedidos del despacho a EN_TRANSITO
                despacho.getDetalles().forEach(det -> {
                    det.getPedido().setEstado(EstadoPedido.EN_TRANSITO);
                    det.getPedido().setFechaSalidaExterior(ahora);
                    pedidoRepository.save(det.getPedido());
                });
            }
            case RECIBIDO -> {
                despacho.setFechaLlegadaReal(ahora);
                // Actualizar pedidos según si llegaron a Ecuador o a otra sede
                despacho.getDetalles().forEach(det -> {
                    Pedido pedido = det.getPedido();
                    // Si el destino es una sucursal nacional → llegó a Ecuador
                    String tipoDest = despacho.getSucursalDestino().getTipo().name();
                    if (tipoDest.equals("MATRIZ")) {
                        pedido.setEstado(EstadoPedido.RECIBIDO_EN_MATRIZ);
                        pedido.setFechaLlegadaEcuador(ahora);
                    } else if (tipoDest.equals("NACIONAL")) {
                        pedido.setEstado(EstadoPedido.DISPONIBLE_EN_SUCURSAL);
                        pedido.setFechaDisponible(ahora);
                    } else {
                        // Llegó a otra sede internacional → sigue en consolidación
                        pedido.setEstado(EstadoPedido.RECIBIDO_EN_SEDE);
                    }
                    pedidoRepository.save(pedido);
                });
            }
            case PROCESADO -> {
                // Verificar que todos los pedidos estén entregados o disponibles
            }
            case CANCELADO -> {
                // Revertir pedidos a RECIBIDO_EN_SEDE
                despacho.getDetalles().forEach(det -> {
                    det.getPedido().setEstado(EstadoPedido.RECIBIDO_EN_SEDE);
                    pedidoRepository.save(det.getPedido());
                });
            }
        }

        despacho.setEstado(nuevoEstado);
        if (observacion != null && !observacion.isBlank()) {
            despacho.setObservaciones(observacion);
        }

        return DespachoResponse.from(despachoRepository.save(despacho));
    }

    // ─── Actualizar información del transporte ────────────────────────────────
    @Transactional
    public DespachoResponse actualizarTransporte(UUID id, DespachoRequest req) {
        Despacho d = getDespachoOrThrow(id);
        d.setAerolinea(req.aerolinea());
        d.setNumeroVuelo(req.numeroVuelo());
        d.setGuiaAerea(req.guiaAerea());
        d.setNumeroContenedor(req.numeroContenedor());
        d.setTipoTransporte(req.tipoTransporte());
        d.setFechaSalidaProgramada(req.fechaSalidaProgramada());
        d.setFechaLlegadaProgramada(req.fechaLlegadaProgramada());
        d.setObservaciones(req.observaciones());
        return DespachoResponse.from(despachoRepository.save(d));
    }

    // ─── Listar todos ─────────────────────────────────────────────────────────
    public List<DespachoResponse> findAll() {
        return despachoRepository.findAll()
                .stream().map(DespachoResponse::from).collect(Collectors.toList());
    }

    // ─── Buscar por ID ────────────────────────────────────────────────────────
    public DespachoResponse findById(UUID id) {
        return DespachoResponse.from(getDespachoOrThrow(id));
    }

    // ─── Buscar por número ────────────────────────────────────────────────────
    public DespachoResponse findByNumero(String numero) {
        return DespachoResponse.from(
                despachoRepository.findByNumeroDespacho(numero)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Despacho no encontrado: " + numero))
        );
    }

    // ─── Por estado ───────────────────────────────────────────────────────────
    public List<DespachoResponse> findByEstado(EstadoDespacho estado) {
        return despachoRepository.findByEstado(estado)
                .stream().map(DespachoResponse::from).collect(Collectors.toList());
    }

    // ─── Despachos abiertos en una sucursal ──────────────────────────────────
    public List<DespachoResponse> findAbiertosEnSucursal(UUID sucursalId) {
        return despachoRepository.findAbiertosEnSucursal(sucursalId)
                .stream().map(DespachoResponse::from).collect(Collectors.toList());
    }

    // ─── Despachos en tránsito hacia una sucursal ─────────────────────────────
    public List<DespachoResponse> findEnTransitoHacia(UUID sucursalId) {
        return despachoRepository.findEnTransitoHacia(sucursalId)
                .stream().map(DespachoResponse::from).collect(Collectors.toList());
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private void recalcularTotales(Despacho despacho) {
        List<DespachoDetalle> detalles = detalleRepository.findByDespachoId(despacho.getId());
        despacho.setTotalPedidos(detalles.size());
        despacho.setPesoTotal(detalles.stream()
                .mapToDouble(d -> d.getPedido().getPeso() != null ? d.getPedido().getPeso() : 0)
                .sum());
        despacho.setValorTotalDeclarado(detalles.stream()
                .mapToDouble(d -> d.getPedido().getValorDeclarado() != null ?
                        d.getPedido().getValorDeclarado() : 0)
                .sum());
    }

    private void validarTransicion(EstadoDespacho actual, EstadoDespacho nuevo) {
        boolean valida = switch (actual) {
            case ABIERTO    -> nuevo == EstadoDespacho.CERRADO || nuevo == EstadoDespacho.CANCELADO;
            case CERRADO    -> nuevo == EstadoDespacho.EN_TRANSITO || nuevo == EstadoDespacho.CANCELADO;
            case EN_TRANSITO -> nuevo == EstadoDespacho.RECIBIDO;
            case RECIBIDO   -> nuevo == EstadoDespacho.PROCESADO;
            default         -> false;
        };
        if (!valida) {
            throw new IllegalArgumentException(
                    "Transición de estado no permitida: " + actual + " → " + nuevo);
        }
    }

    private String generarNumeroDespacho() {
        String anio = String.valueOf(Year.now().getValue());
        long total = despachoRepository.count() + 1;
        String numero = String.format("DES-%s-%05d", anio, total);
        while (despachoRepository.existsByNumeroDespacho(numero)) {
            total++;
            numero = String.format("DES-%s-%05d", anio, total);
        }
        return numero;
    }

    private Despacho getDespachoOrThrow(UUID id) {
        return despachoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Despacho no encontrado: " + id));
    }
}
