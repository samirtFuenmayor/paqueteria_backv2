package com.equalatam.equlatam_backv2.cotizador.service;


import com.equalatam.equlatam_backv2.cotizador.dto.request.CotizadorRequest;
import com.equalatam.equlatam_backv2.cotizador.dto.response.CotizadorResponse;
import com.equalatam.equlatam_backv2.exception.ResourceNotFoundException;
import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import com.equalatam.equlatam_backv2.sucursales.repository.SucursalRepository;
import com.equalatam.equlatam_backv2.tarifas.entity.Tarifa;
import com.equalatam.equlatam_backv2.tarifas.repository.TarifaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CotizadorService {

    private final SucursalRepository sucursalRepository;
    private final TarifaRepository tarifaRepository;

    // Valores por defecto si no hay tarifa configurada
    private static final double TARIFA_DEFAULT_USA      = 3.50;
    private static final double TARIFA_DEFAULT_CANADA   = 4.00;
    private static final double COSTO_MANEJO_DEFAULT    = 5.00;
    private static final double PORCENTAJE_SEGURO       = 0.02;
    private static final double DIVISOR_VOLUMETRICO     = 166.0;

    public CotizadorResponse cotizar(CotizadorRequest req) {

        Sucursal origen = sucursalRepository.findById(req.sucursalOrigenId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sucursal origen no encontrada"));

        Sucursal destino = sucursalRepository.findById(req.sucursalDestinoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sucursal destino no encontrada"));

        // ─── Calcular peso volumétrico ────────────────────────────────────────
        double pesoReal = req.peso();
        double pesoVolumetrico = 0.0;

        if (req.largo() != null && req.ancho() != null && req.alto() != null) {
            pesoVolumetrico = (req.largo() * req.ancho() * req.alto()) / DIVISOR_VOLUMETRICO;
            pesoVolumetrico = round(pesoVolumetrico);
        }

        double pesoCobrable = Math.max(pesoReal, pesoVolumetrico);
        String criterioPeso = pesoVolumetrico > pesoReal ? "VOLUMETRICO" : "REAL";

        // ─── Obtener tarifa por libra desde BD ────────────────────────────────
        double tarifaPorLibra = obtenerTarifaPorLibra(origen);

        // ─── Obtener costo de manejo ──────────────────────────────────────────
        double costoManejo = obtenerCostoManejo(origen);

        // ─── Calcular flete ───────────────────────────────────────────────────
        double costoFlete = round(pesoCobrable * tarifaPorLibra);

        // ─── Calcular seguro ──────────────────────────────────────────────────
        double costoSeguro = 0.0;
        if (req.valorDeclarado() != null && req.valorDeclarado() > 0) {
            costoSeguro = round(req.valorDeclarado() * PORCENTAJE_SEGURO);
        }

        // ─── Total ────────────────────────────────────────────────────────────
        double subtotal = round(costoFlete + costoManejo);
        double total    = round(subtotal + costoSeguro);

        // ─── Nota informativa ─────────────────────────────────────────────────
        String nota = construirNota(criterioPeso, pesoReal, pesoVolumetrico, origen);

        return new CotizadorResponse(
                origen.getNombre(),
                origen.getPais(),
                destino.getNombre(),
                destino.getCiudad(),

                pesoReal,
                pesoVolumetrico > 0 ? pesoVolumetrico : null,
                round(pesoCobrable),
                criterioPeso,

                tarifaPorLibra,
                costoFlete,
                costoManejo,
                costoSeguro > 0 ? costoSeguro : null,
                subtotal,
                total,

                "USD",
                nota
        );
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private double obtenerTarifaPorLibra(Sucursal origen) {
        Tarifa tarifa = tarifaRepository.findTarifaPorLibra(origen.getId());
        if (tarifa != null && tarifa.getValorFijo() != null) {
            return tarifa.getValorFijo();
        }
        // Fallback a tarifas default según país
        String pais = origen.getPais().toLowerCase();
        return pais.contains("canad") ? TARIFA_DEFAULT_CANADA : TARIFA_DEFAULT_USA;
    }

    private double obtenerCostoManejo(Sucursal origen) {
        return tarifaRepository.findByTipoAndActivaTrue(
                        com.equalatam.equlatam_backv2.tarifas.entity.TipoTarifa.MANEJO)
                .stream()
                .filter(t -> t.getSucursalOrigen() == null ||
                        t.getSucursalOrigen().getId().equals(origen.getId()))
                .findFirst()
                .map(t -> t.getValorFijo() != null ? t.getValorFijo() : COSTO_MANEJO_DEFAULT)
                .orElse(COSTO_MANEJO_DEFAULT);
    }

    private String construirNota(String criterio, double real, double vol, Sucursal origen) {
        StringBuilder sb = new StringBuilder();
        if ("VOLUMETRICO".equals(criterio)) {
            sb.append(String.format(
                    "Se cobra por peso volumétrico (%.2f lbs) ya que es mayor al peso real (%.2f lbs). ",
                    vol, real));
        }
        sb.append("Tarifa válida para envíos desde ").append(origen.getPais()).append(". ");
        sb.append("El seguro es opcional y cubre el 2% del valor declarado.");
        return sb.toString();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}