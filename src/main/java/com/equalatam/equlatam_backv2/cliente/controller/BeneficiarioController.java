package com.equalatam.equlatam_backv2.cliente.controller;

import com.equalatam.equlatam_backv2.cliente.Enums;
import com.equalatam.equlatam_backv2.cliente.dto.request.BeneficiarioCreateRequest;
import com.equalatam.equlatam_backv2.cliente.entity.Beneficiario;
import com.equalatam.equlatam_backv2.cliente.service.BeneficiarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/beneficiarios")
@RequiredArgsConstructor
public class BeneficiarioController {

    private final BeneficiarioService service;

    @PostMapping("/titular/{identificacion}")
    public Beneficiario add(
            @PathVariable String identificacion,
            @RequestBody BeneficiarioCreateRequest r
    ) {
        return service.addToTitular(identificacion, r);
    }

    @PutMapping("/{id}/estado")
    public Beneficiario cambiarEstado(
            @PathVariable UUID id,
            @RequestParam Enums.EstadoPersona estado
    ) {
        return service.cambiarEstado(id, estado);
    }
}
