package com.equalatam.equlatam_backv2.financiero.service;

import com.equalatam.equlatam_backv2.cliente.repositories.ClienteRepository;
import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.financiero.dto.FacturaDetalleRequest;
import com.equalatam.equlatam_backv2.financiero.dto.FacturaDetalleResponse;
import com.equalatam.equlatam_backv2.financiero.dto.FacturaRequest;
import com.equalatam.equlatam_backv2.financiero.dto.FacturaResponse;
import com.equalatam.equlatam_backv2.financiero.entity.*;
import com.equalatam.equlatam_backv2.financiero.enums.*;
import com.equalatam.equlatam_backv2.financiero.repository.*;
import com.equalatam.equlatam_backv2.pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FacturaService {

    private final FacturaRepository    facturaRepo;
    private final CotizacionRepository cotizacionRepo;
    private final ClienteRepository    clienteRepo;
    private final PedidoRepository     pedidoRepo;
    private final NumeroSecuencialService secuencialService;

    // Datos de tu empresa — idealmente en application.properties o tabla config
    private static final String EMISOR_RUC          = "1792XXXXXXXXx001";
    private static final String EMISOR_RAZON_SOCIAL  = "EQUALATAM S.A.";
    private static final String EMISOR_DIRECCION     = "Quito, Ecuador";
    private static final String EST_DEFAULT          = "001";
    private static final String PUNTO_DEFAULT        = "001";

    // ─── Crear factura en borrador ────────────────────────────────────────────

    @Transactional
    public Factura crear(FacturaRequest req, User operador) {

        Factura f = new Factura();

        String est   = req.getEstablecimiento() != null ? req.getEstablecimiento() : EST_DEFAULT;
        String punto = req.getPuntoEmision()    != null ? req.getPuntoEmision()    : PUNTO_DEFAULT;

        f.setEstablecimiento(est);
        f.setPuntoEmision(punto);
        f.setTipoDocumento(TipoDocumento.FACTURA);
        f.setEstado(EstadoFactura.BORRADOR);
        f.setFechaEmision(LocalDate.now());
        f.setFechaVencimiento(req.getFechaVencimiento());

        f.setCliente(clienteRepo.findById(req.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado")));

        if (req.getPedidoId() != null) {
            f.setPedido(pedidoRepo.findById(req.getPedidoId())
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado")));
        }

        // Si viene de cotización, cargar su detalle
        if (req.getCotizacionId() != null) {
            Cotizacion cot = cotizacionRepo.findById(req.getCotizacionId())
                    .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

            if (cot.getEstado() != EstadoCotizacion.APROBADA &&
                    cot.getEstado() != EstadoCotizacion.PENDIENTE) {
                throw new RuntimeException(
                        "La cotización debe estar en estado APROBADA o PENDIENTE para facturar");
            }

            f.setCotizacion(cot);

            // Crear línea de detalle desde la cotización
            FacturaDetalle detalle = new FacturaDetalle(
                    f,
                    buildDescripcionServicio(cot),
                    1.0,
                    cot.getSubtotal(),
                    true // grava IVA 15%
            );
            f.getDetalles().add(detalle);

            // Marcar cotización como facturada
            cot.setEstado(EstadoCotizacion.FACTURADA);
            cotizacionRepo.save(cot);

        } else if (req.getDetalles() != null && !req.getDetalles().isEmpty()) {
            // Detalles manuales
            int orden = 1;
            for (FacturaDetalleRequest dr : req.getDetalles()) {
                FacturaDetalle det = new FacturaDetalle();
                det.setFactura(f);
                det.setDescripcion(dr.getDescripcion());
                det.setCantidad(dr.getCantidad() != null ? dr.getCantidad() : 1.0);
                det.setPrecioUnitario(dr.getPrecioUnitario());
                det.setDescuento(dr.getDescuento() != null ? dr.getDescuento() : 0.0);
                det.setGravaIva(dr.getGravaIva() == null || dr.getGravaIva());
                det.setOrden(orden++);
                f.getDetalles().add(det);
            }
        }

        if (req.getFormaPago() != null) {
            f.setFormaPago(FormaPago.valueOf(req.getFormaPago()));
        }

        f.setObservaciones(req.getObservaciones());
        f.setEmitidoPor(operador);
        f.setEmisorRuc(EMISOR_RUC);
        f.setEmisorRazonSocial(EMISOR_RAZON_SOCIAL);
        f.setEmisorDireccion(EMISOR_DIRECCION);

        // Calcular totales
        f.recalcularTotales();

        return facturaRepo.save(f);
    }

    // ─── Emitir factura (BORRADOR → EMITIDA, asigna número SRI) ──────────────

    @Transactional
    public Factura emitir(UUID id, User operador) {
        Factura f = obtener(id);

        if (f.getEstado() != EstadoFactura.BORRADOR) {
            throw new RuntimeException("Solo se pueden emitir facturas en estado BORRADOR");
        }
        if (f.getDetalles().isEmpty()) {
            throw new RuntimeException("La factura no tiene detalles");
        }

        // Asignar número SRI secuencial
        long sec = secuencialService.siguienteSecuencialFactura(
                f.getEstablecimiento(), f.getPuntoEmision());
        f.setSecuencial(sec);
        f.setNumeroFactura(secuencialService.formatearNumeroFactura(
                f.getEstablecimiento(), f.getPuntoEmision(), sec));

        f.setEstado(EstadoFactura.EMITIDA);
        f.setFechaEmision(LocalDate.now());
        f.setEmitidoPor(operador);

        // Si no tiene vencimiento, dar 30 días
        if (f.getFechaVencimiento() == null) {
            f.setFechaVencimiento(LocalDate.now().plusDays(30));
        }

        return facturaRepo.save(f);
    }

    // ─── Anular factura ───────────────────────────────────────────────────────

    @Transactional
    public Factura anular(UUID id, String motivo, User operador) {
        Factura f = obtener(id);

        if (f.getEstado() == EstadoFactura.PAGADA) {
            throw new RuntimeException(
                    "Una factura pagada no puede anularse directamente. Emite una nota de crédito.");
        }
        if (f.getEstado() == EstadoFactura.ANULADA) {
            throw new RuntimeException("La factura ya está anulada");
        }

        f.setEstado(EstadoFactura.ANULADA);
        f.setObservaciones((f.getObservaciones() != null ? f.getObservaciones() + " | " : "")
                + "ANULADA: " + motivo);

        return facturaRepo.save(f);
    }

    // ─── Marcar como pagada (lo hace PagoService automáticamente) ─────────────

    @Transactional
    public void marcarPagada(Factura f) {
        f.setEstado(EstadoFactura.PAGADA);
        facturaRepo.save(f);
    }

    // ─── Consultas ────────────────────────────────────────────────────────────

    public List<Factura> listarPorCliente(UUID clienteId) {
        return facturaRepo.findByClienteIdOrderByCreadoEnDesc(clienteId);
    }

    public List<Factura> listarPorPedido(UUID pedidoId) {
        return facturaRepo.findByPedidoId(pedidoId);
    }

    public List<Factura> listarPendientes() {
        return facturaRepo.findByEstado(EstadoFactura.EMITIDA);
    }

    public List<Factura> listarPorRango(LocalDate desde, LocalDate hasta) {
        return facturaRepo.findByRangoFechas(desde, hasta);
    }

    public Factura obtener(UUID id) {
        return facturaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + id));
    }

    public Double getDeudaCliente(UUID clienteId) {
        return facturaRepo.sumDeudaCliente(clienteId);
    }

    // ─── Marcar vencidas (para scheduler diario) ──────────────────────────────

    @Transactional
    public void marcarVencidas() {
        facturaRepo.findVencidas(LocalDate.now())
                .forEach(f -> {
                    f.setEstado(EstadoFactura.VENCIDA);
                    facturaRepo.save(f);
                });
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private String buildDescripcionServicio(Cotizacion cot) {
        StringBuilder sb = new StringBuilder();
        sb.append("Servicio de ")
                .append(cot.getPedido() != null ? cot.getPedido().getTipo().name().toLowerCase() : "envío");
        if (cot.getPedido() != null) {
            sb.append(" - Pedido ").append(cot.getPedido().getNumeroPedido());
        }
        if (cot.getPesoFacturable() != null) {
            sb.append(" - ").append(String.format("%.2f", cot.getPesoFacturable())).append(" lbs");
        }
        return sb.toString();
    }


    public FacturaResponse toResponse(Factura f) {
        FacturaResponse r = new FacturaResponse();
        r.setId(f.getId());
        r.setNumeroFactura(f.getNumeroFactura());
        r.setTipoDocumento(f.getTipoDocumento() != null ? f.getTipoDocumento().name() : null);
        r.setEstado(f.getEstado() != null ? f.getEstado().name() : null);
        r.setFormaPago(f.getFormaPago() != null ? f.getFormaPago().name() : null);
        r.setSubtotal0(f.getSubtotal0());
        r.setSubtotal15(f.getSubtotal15());
        r.setDescuento(f.getDescuento());
        r.setIva(f.getIva());
        r.setTotal(f.getTotal());
        r.setFechaEmision(f.getFechaEmision());
        r.setFechaVencimiento(f.getFechaVencimiento());
        r.setObservaciones(f.getObservaciones());
        r.setEmisorRuc(f.getEmisorRuc());
        r.setEmisorRazonSocial(f.getEmisorRazonSocial());
        r.setCreadoEn(f.getCreadoEn() != null ? f.getCreadoEn().toString() : null);
        if (f.getCliente() != null) {
            r.setClienteNombre(f.getCliente().getNombres() + " " + f.getCliente().getApellidos());
            r.setClienteIdentificacion(f.getCliente().getNumeroIdentificacion());
            r.setClienteEmail(f.getCliente().getEmail());
            r.setClienteDireccion(f.getCliente().getDireccion());
        }
        if (f.getPedido() != null) {
            r.setPedidoNumero(f.getPedido().getNumeroPedido());
        }
        if (f.getDetalles() != null) {
            r.setDetalles(f.getDetalles().stream().map(d -> {
                FacturaDetalleResponse dr = new FacturaDetalleResponse();
                dr.setId(d.getId());
                dr.setDescripcion(d.getDescripcion());
                dr.setCantidad(d.getCantidad());
                dr.setPrecioUnitario(d.getPrecioUnitario());
                dr.setDescuento(d.getDescuento());
                dr.setSubtotal(d.getSubtotal());
                dr.setGravaIva(d.isGravaIva());
                dr.setOrden(d.getOrden());
                return dr;
            }).collect(java.util.stream.Collectors.toList()));
        }
        return r;
    }
}