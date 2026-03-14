package com.equalatam.equlatam_backv2.financiero.service;

import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.financiero.dto.NotaAjusteRequest;
import com.equalatam.equlatam_backv2.financiero.entity.Factura;
import com.equalatam.equlatam_backv2.financiero.entity.NotaAjuste;
import com.equalatam.equlatam_backv2.financiero.enums.EstadoFactura;
import com.equalatam.equlatam_backv2.financiero.enums.TipoNota;
import com.equalatam.equlatam_backv2.financiero.repository.FacturaRepository;
import com.equalatam.equlatam_backv2.financiero.repository.NotaAjusteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotaAjusteService {

    private final NotaAjusteRepository notaRepo;
    private final FacturaRepository    facturaRepo;
    private final NumeroSecuencialService secuencialService;

    private static final double IVA = 0.15;

    @Transactional
    public NotaAjuste emitir(NotaAjusteRequest req, User operador) {

        Factura origen = facturaRepo.findById(req.getFacturaOrigenId())
                .orElseThrow(() -> new RuntimeException("Factura origen no encontrada"));

        if (origen.getEstado() == EstadoFactura.BORRADOR) {
            throw new RuntimeException("No se puede emitir nota sobre una factura en borrador");
        }
        if (origen.getEstado() == EstadoFactura.ANULADA) {
            throw new RuntimeException("La factura origen ya está anulada");
        }

        TipoNota tipo = TipoNota.valueOf(req.getTipo());

        NotaAjuste n = new NotaAjuste();
        n.setTipo(tipo);
        n.setFacturaOrigen(origen);
        n.setMotivo(req.getMotivo());
        n.setFechaEmision(LocalDate.now());
        n.setEstado(EstadoFactura.EMITIDA);
        n.setEmitidoPor(operador);

        // Establecimiento y punto (mismos que la factura origen)
        n.setEstablecimiento(origen.getEstablecimiento());
        n.setPuntoEmision(origen.getPuntoEmision());

        long sec = secuencialService.siguienteSecuencialNota(
                origen.getEstablecimiento(), origen.getPuntoEmision(), tipo);
        n.setSecuencial(sec);
        n.setNumeroNota(secuencialService.formatearNumeroNota(
                tipo, origen.getEstablecimiento(), origen.getPuntoEmision(), sec));

        // Calcular montos
        double subtotal = req.getMonto() != null ? req.getMonto() : 0.0;
        double ivaAmt   = (req.getAplicaIva() != null && req.getAplicaIva())
                ? Math.round(subtotal * IVA * 100.0) / 100.0
                : 0.0;
        n.setSubtotal(subtotal);
        n.setIva(ivaAmt);
        n.setTotal(Math.round((subtotal + ivaAmt) * 100.0) / 100.0);

        // Si es nota de crédito total, marcar factura como anulada
        if (tipo == TipoNota.CREDITO
                && req.getMonto() != null
                && req.getMonto() >= origen.getTotal()) {
            origen.setEstado(EstadoFactura.ANULADA);
            facturaRepo.save(origen);
        }

        return notaRepo.save(n);
    }

    public List<NotaAjuste> listarPorFactura(UUID facturaId) {
        return notaRepo.findByFacturaOrigenId(facturaId);
    }

    public NotaAjuste obtener(UUID id) {
        return notaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Nota de ajuste no encontrada"));
    }

    @Transactional
    public NotaAjuste anular(UUID id) {
        NotaAjuste n = obtener(id);
        if (n.getEstado() == EstadoFactura.ANULADA) {
            throw new RuntimeException("La nota ya está anulada");
        }
        n.setEstado(EstadoFactura.ANULADA);
        return notaRepo.save(n);
    }
}