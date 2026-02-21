package com.equalatam.equlatam_backv2.cotizador.controller;

import com.equalatam.equlatam_backv2.cotizador.dto.request.CotizadorRequest;
import com.equalatam.equlatam_backv2.cotizador.dto.response.CotizadorResponse;
import com.equalatam.equlatam_backv2.cotizador.service.CotizadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CotizadorController {

    private final CotizadorService cotizadorService;

    // ─── Público: cliente cotiza sin JWT ──────────────────────────────────────
    @PostMapping("/api/cotizador/public")
    public ResponseEntity<CotizadorResponse> cotizarPublico(
            @Valid @RequestBody CotizadorRequest req) {
        return ResponseEntity.ok(cotizadorService.cotizar(req));
    }

    // ─── Privado: empleado cotiza con JWT ─────────────────────────────────────
    @PostMapping("/api/cotizador")
    public ResponseEntity<CotizadorResponse> cotizar(
            @Valid @RequestBody CotizadorRequest req) {
        return ResponseEntity.ok(cotizadorService.cotizar(req));
    }
}
