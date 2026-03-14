// ─── PagoService.java ─────────────────────────────────────────────────────────
package com.equalatam.equlatam_backv2.financiero.service;

import com.equalatam.equlatam_backv2.cliente.repositories.ClienteRepository;
import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.financiero.dto.PagoRequest;
import com.equalatam.equlatam_backv2.financiero.entity.Factura;
import com.equalatam.equlatam_backv2.financiero.entity.Pago;
import com.equalatam.equlatam_backv2.financiero.enums.EstadoFactura;
import com.equalatam.equlatam_backv2.financiero.enums.EstadoPago;
import com.equalatam.equlatam_backv2.financiero.enums.FormaPago;
import com.equalatam.equlatam_backv2.financiero.repository.FacturaRepository;
import com.equalatam.equlatam_backv2.financiero.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository        pagoRepo;
    private final FacturaRepository     facturaRepo;
    private final ClienteRepository     clienteRepo;
    private final NumeroSecuencialService secuencialService;
    private final FacturaService        facturaService;

    // ─── Registrar pago (queda en PENDIENTE hasta que admin confirme) ─────────

    @Transactional
    public Pago registrar(PagoRequest req, User operador) {

        Factura factura = facturaRepo.findById(req.getFacturaId())
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        if (factura.getEstado() == EstadoFactura.PAGADA) {
            throw new RuntimeException("La factura ya está pagada");
        }
        if (factura.getEstado() == EstadoFactura.ANULADA) {
            throw new RuntimeException("No se puede registrar pago de una factura anulada");
        }
        if (factura.getEstado() == EstadoFactura.BORRADOR) {
            throw new RuntimeException("La factura debe estar emitida para recibir pagos");
        }

        Pago p = new Pago();
        p.setNumeroPago(secuencialService.siguienteNumeroPago());
        p.setFactura(factura);
        p.setCliente(factura.getCliente());
        p.setMonto(req.getMonto());
        p.setFormaPago(FormaPago.valueOf(req.getFormaPago()));
        p.setReferencia(req.getReferencia());
        p.setBanco(req.getBanco());
        p.setComprobanteUrl(req.getComprobanteUrl());
        p.setObservaciones(req.getObservaciones());
        p.setEstado(EstadoPago.PENDIENTE);
        p.setFechaPago(req.getFechaPago() != null
                ? LocalDateTime.parse(req.getFechaPago())
                : LocalDateTime.now());
        p.setRegistradoPor(operador);

        return pagoRepo.save(p);
    }

    // ─── Confirmar pago (admin) ───────────────────────────────────────────────

    @Transactional
    public Pago confirmar(UUID pagoId, User admin) {
        Pago p = obtener(pagoId);

        if (p.getEstado() != EstadoPago.PENDIENTE) {
            throw new RuntimeException("Solo se pueden confirmar pagos en estado PENDIENTE");
        }

        p.setEstado(EstadoPago.CONFIRMADO);
        p.setFechaConfirmacion(LocalDateTime.now());
        p.setConfirmadoPor(admin);
        pagoRepo.save(p);

        // Verificar si la factura quedó totalmente pagada
        Double totalPagado = pagoRepo.sumPagadoFactura(p.getFactura().getId());
        if (totalPagado >= p.getFactura().getTotal()) {
            facturaService.marcarPagada(p.getFactura());
        }

        return p;
    }

    // ─── Rechazar pago ────────────────────────────────────────────────────────

    @Transactional
    public Pago rechazar(UUID pagoId, String motivo, User admin) {
        Pago p = obtener(pagoId);

        if (p.getEstado() != EstadoPago.PENDIENTE) {
            throw new RuntimeException("Solo se pueden rechazar pagos en estado PENDIENTE");
        }

        p.setEstado(EstadoPago.RECHAZADO);
        p.setMotivoRechazo(motivo);
        p.setConfirmadoPor(admin);
        return pagoRepo.save(p);
    }

    // ─── Consultas ────────────────────────────────────────────────────────────

    public List<Pago> listarPorFactura(UUID facturaId) {
        return pagoRepo.findByFacturaId(facturaId);
    }

    public List<Pago> listarPorCliente(UUID clienteId) {
        return pagoRepo.findByClienteIdOrderByCreadoEnDesc(clienteId);
    }

    public List<Pago> listarPendientes() {
        return pagoRepo.findByEstado(EstadoPago.PENDIENTE);
    }

    public Pago obtener(UUID id) {
        return pagoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado: " + id));
    }
}