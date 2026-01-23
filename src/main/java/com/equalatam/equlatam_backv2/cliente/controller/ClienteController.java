package com.equalatam.equlatam_backv2.cliente.controller;

import com.equalatam.equlatam_backv2.cliente.Enums;
import com.equalatam.equlatam_backv2.cliente.dto.request.ClienteCreateRequest;
import com.equalatam.equlatam_backv2.cliente.entity.Cliente;
import com.equalatam.equlatam_backv2.cliente.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService service;

    @PostMapping
    public Cliente create(@RequestBody ClienteCreateRequest r) {
        return service.create(r);
    }

    @GetMapping("/buscar/{identificacion}")
    public Cliente buscar(@PathVariable String identificacion) {
        return service.findByIdentificacion(identificacion);
    }

    @PutMapping("/{id}/estado")
    public Cliente cambiarEstado(
            @PathVariable UUID id,
            @RequestParam Enums.EstadoPersona estado
    ) {
        return service.cambiarEstado(id, estado);
    }
}
