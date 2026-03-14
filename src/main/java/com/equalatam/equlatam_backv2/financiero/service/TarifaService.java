package com.equalatam.equlatam_backv2.financiero.service;

import com.equalatam.equlatam_backv2.financiero.dto.TarifaRequest;
import com.equalatam.equlatam_backv2.financiero.entity.Tarifa;
import com.equalatam.equlatam_backv2.financiero.enums.CategoriaPaquete;
import com.equalatam.equlatam_backv2.financiero.repository.TarifaRepository;
import com.equalatam.equlatam_backv2.pedidos.entity.TipoPedido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TarifaService {

    private final TarifaRepository repo;

    // ─── CRUD ─────────────────────────────────────────────────────────────────

    @Transactional
    public Tarifa crear(TarifaRequest req) {
        Tarifa t = new Tarifa();
        mapear(req, t);
        return repo.save(t);
    }

    @Transactional
    public Tarifa actualizar(UUID id, TarifaRequest req) {
        Tarifa t = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada: " + id));
        mapear(req, t);
        return repo.save(t);
    }

    @Transactional
    public void desactivar(UUID id) {
        Tarifa t = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada: " + id));
        t.setActivo(false);
        repo.save(t);
    }

    public List<Tarifa> listarActivas() {
        return repo.findByActivoTrue();
    }

    public List<Tarifa> listarTodas() {
        return repo.findAll();
    }

    public Tarifa obtener(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada: " + id));
    }

    // ─── Búsqueda automática de tarifa aplicable ──────────────────────────────

    public Optional<Tarifa> buscarTarifaAplicable(CategoriaPaquete categoria,
                                                  TipoPedido tipoPedido,
                                                  Double peso) {
        return repo.findTarifaAplicable(categoria, tipoPedido, peso, LocalDate.now());
    }

    // ─── Cálculo de precio ────────────────────────────────────────────────────

    public record ResultadoCalculo(
            Tarifa tarifa,
            double pesoReal,
            double pesoVolumetrico,
            double pesoFacturable,
            double subtotal,
            double iva,
            double total,
            String desglose
    ) {}

    public ResultadoCalculo calcular(Tarifa tarifa,
                                     Double pesoReal,
                                     Double largo, Double ancho, Double alto,
                                     Double valorDeclarado) {

        double pReal = pesoReal != null ? pesoReal : 0.0;
        double pVol  = 0.0;

        if (largo != null && ancho != null && alto != null
                && tarifa.getFactorDivisorVolumetrico() != null
                && tarifa.getFactorDivisorVolumetrico() > 0) {
            pVol = (largo * ancho * alto) / tarifa.getFactorDivisorVolumetrico();
            pVol = Math.round(pVol * 100.0) / 100.0;
        }

        double pesoFacturable = Math.max(pReal, pVol);
        if (tarifa.getPesoMinimo() != null && tarifa.getPesoMinimo() > 0) {
            pesoFacturable = Math.max(pesoFacturable, tarifa.getPesoMinimo());
        }

        // Desglose del cálculo
        StringBuilder sb = new StringBuilder();

        double subtotal = tarifa.getPrecioBase();
        sb.append("Precio base: $").append(fmt(tarifa.getPrecioBase()));

        if (tarifa.getPrecioPorLibra() != null && tarifa.getPrecioPorLibra() > 0) {
            double costolbs = pesoFacturable * tarifa.getPrecioPorLibra();
            subtotal += costolbs;
            sb.append(" | ").append(fmt(pesoFacturable))
                    .append(" lbs × $").append(fmt(tarifa.getPrecioPorLibra()))
                    .append(" = $").append(fmt(costolbs));
        }

        if (tarifa.getPrecioPorCm3() != null && tarifa.getPrecioPorCm3() > 0
                && largo != null && ancho != null && alto != null) {
            double vol    = largo * ancho * alto;
            double costoV = vol * tarifa.getPrecioPorCm3();
            subtotal += costoV;
            sb.append(" | Volumen ").append(fmt(vol))
                    .append(" cm³ × $").append(fmt(tarifa.getPrecioPorCm3()))
                    .append(" = $").append(fmt(costoV));
        }

        if (tarifa.getPorcentajeSobreValorDeclarado() != null
                && tarifa.getPorcentajeSobreValorDeclarado() > 0
                && valorDeclarado != null && valorDeclarado > 0) {
            double costoSeg = valorDeclarado * (tarifa.getPorcentajeSobreValorDeclarado() / 100.0);
            subtotal += costoSeg;
            sb.append(" | Seguro ").append(fmt(tarifa.getPorcentajeSobreValorDeclarado()))
                    .append("% sobre $").append(fmt(valorDeclarado))
                    .append(" = $").append(fmt(costoSeg));
        }

        subtotal = Math.round(subtotal * 100.0) / 100.0;
        double pctIva = tarifa.getPorcentajeIva() != null ? tarifa.getPorcentajeIva() : 15.0;
        double iva    = Math.round(subtotal * (pctIva / 100.0) * 100.0) / 100.0;
        double total  = Math.round((subtotal + iva) * 100.0) / 100.0;

        sb.append(" | Subtotal: $").append(fmt(subtotal))
                .append(" | IVA ").append(fmt(pctIva)).append("%: $").append(fmt(iva))
                .append(" | TOTAL: $").append(fmt(total));

        return new ResultadoCalculo(tarifa, pReal, pVol, pesoFacturable,
                subtotal, iva, total, sb.toString());
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void mapear(TarifaRequest req, Tarifa t) {
        if (req.getNombre()    != null) t.setNombre(req.getNombre());
        if (req.getDescripcion() != null) t.setDescripcion(req.getDescripcion());
        if (req.getCategoria() != null) t.setCategoria(req.getCategoria());
        if (req.getTipoPedido() != null) t.setTipoPedido(req.getTipoPedido());
        if (req.getPrecioBase() != null) t.setPrecioBase(req.getPrecioBase());
        if (req.getPrecioPorLibra() != null) t.setPrecioPorLibra(req.getPrecioPorLibra());
        if (req.getPesoMinimo() != null) t.setPesoMinimo(req.getPesoMinimo());
        if (req.getPrecioPorCm3() != null) t.setPrecioPorCm3(req.getPrecioPorCm3());
        if (req.getFactorDivisorVolumetrico() != null)
            t.setFactorDivisorVolumetrico(req.getFactorDivisorVolumetrico());
        if (req.getPorcentajeSobreValorDeclarado() != null)
            t.setPorcentajeSobreValorDeclarado(req.getPorcentajeSobreValorDeclarado());
        if (req.getPesoDesde() != null) t.setPesoDesde(req.getPesoDesde());
        if (req.getPesoHasta() != null) t.setPesoHasta(req.getPesoHasta());
        if (req.getPorcentajeIva() != null) t.setPorcentajeIva(req.getPorcentajeIva());
        if (req.getVigenciaDesde() != null) t.setVigenciaDesde(req.getVigenciaDesde());
        if (req.getVigenciaHasta() != null) t.setVigenciaHasta(req.getVigenciaHasta());
        if (req.getActivo() != null) t.setActivo(req.getActivo());
    }

    private String fmt(double v) {
        return String.format("%.2f", v);
    }
}