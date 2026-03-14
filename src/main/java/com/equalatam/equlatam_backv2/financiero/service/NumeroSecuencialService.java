package com.equalatam.equlatam_backv2.financiero.service;

import com.equalatam.equlatam_backv2.financiero.enums.TipoDocumento;
import com.equalatam.equlatam_backv2.financiero.enums.TipoNota;
import com.equalatam.equlatam_backv2.financiero.repository.CotizacionRepository;
import com.equalatam.equlatam_backv2.financiero.repository.FacturaRepository;
import com.equalatam.equlatam_backv2.financiero.repository.NotaAjusteRepository;
import com.equalatam.equlatam_backv2.financiero.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Genera y formatea los números de secuencia para todos los documentos.
 *
 * Formatos:
 *  Factura SRI:       001-001-000000001
 *  Nota de crédito:   NC-001-001-000000001
 *  Nota de débito:    ND-001-001-000000001
 *  Cotización:        COT-2026-00001
 *  Pago:              PAG-2026-00001
 */
@Service
@RequiredArgsConstructor
public class NumeroSecuencialService {

    private final FacturaRepository    facturaRepo;
    private final CotizacionRepository cotizacionRepo;
    private final PagoRepository       pagoRepo;
    private final NotaAjusteRepository notaRepo;

    // ─── Factura / Nota (formato SRI) ─────────────────────────────────────────

    /**
     * Devuelve el siguiente secuencial para el par establecimiento+puntoEmision.
     * El número formateado sería: est-punto-{9 dígitos}
     * Ejemplo: 001-001-000000001
     */
    public long siguienteSecuencialFactura(String est, String punto) {
        return facturaRepo
                .findUltimoSecuencial(est, punto, TipoDocumento.FACTURA)
                .map(n -> n + 1)
                .orElse(1L);
    }

    public String formatearNumeroFactura(String est, String punto, long sec) {
        return String.format("%s-%s-%09d", est, punto, sec);
    }

    public long siguienteSecuencialNota(String est, String punto, TipoNota tipo) {
        return notaRepo
                .findUltimoSecuencial(est, punto, tipo)
                .map(n -> n + 1)
                .orElse(1L);
    }

    public String formatearNumeroNota(TipoNota tipo, String est, String punto, long sec) {
        String prefijo = tipo == TipoNota.CREDITO ? "NC" : "ND";
        return String.format("%s-%s-%s-%09d", prefijo, est, punto, sec);
    }

    // ─── Cotización ───────────────────────────────────────────────────────────

    public String siguienteNumeroCotizacion() {
        int anio    = LocalDate.now().getYear();
        String pref = "COT-" + anio + "-";
        return cotizacionRepo
                .findUltimoNumero(pref)
                .map(ultimo -> {
                    long num = Long.parseLong(ultimo.replace(pref, "")) + 1;
                    return String.format("%s%05d", pref, num);
                })
                .orElse(String.format("%s%05d", pref, 1));
    }

    // ─── Pago ─────────────────────────────────────────────────────────────────

    public String siguienteNumeroPago() {
        int anio    = LocalDate.now().getYear();
        String pref = "PAG-" + anio + "-";
        return pagoRepo
                .findUltimoNumero(pref)
                .map(ultimo -> {
                    long num = Long.parseLong(ultimo.replace(pref, "")) + 1;
                    return String.format("%s%05d", pref, num);
                })
                .orElse(String.format("%s%05d", pref, 1));
    }
}