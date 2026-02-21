package com.equalatam.equlatam_backv2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

// ─── Agregar esta clase en tu paquete config ──────────────────────────────────
// Habilita el @Async para que los emails se envíen en segundo plano
@Configuration
@EnableAsync
public class AsyncConfig {
    // Solo necesitas esta anotación, Spring se encarga del resto
}
