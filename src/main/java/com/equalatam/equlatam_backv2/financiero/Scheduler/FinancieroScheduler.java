package com.equalatam.equlatam_backv2.financiero.scheduler;

import com.equalatam.equlatam_backv2.financiero.service.CotizacionService;
import com.equalatam.equlatam_backv2.financiero.service.FacturaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinancieroScheduler {

    private final FacturaService    facturaService;
    private final CotizacionService cotizacionService;

    // Ejecuta todos los días a las 01:00 AM
    @Scheduled(cron = "0 0 1 * * *")
    public void procesarVencimientos() {
        log.info("[Financiero] Procesando vencimientos...");
        facturaService.marcarVencidas();
        cotizacionService.vencerExpiradas();
        log.info("[Financiero] Vencimientos procesados correctamente");
    }
}