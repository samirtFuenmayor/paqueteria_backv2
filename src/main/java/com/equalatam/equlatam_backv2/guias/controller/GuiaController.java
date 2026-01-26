package com.equalatam.equlatam_backv2.guias.controller;

import com.equalatam.equlatam_backv2.guias.Enums;
import com.equalatam.equlatam_backv2.guias.dto.request.GuiaCreateRequest;
import com.equalatam.equlatam_backv2.guias.dto.request.GuiaEstadoRequest;
import com.equalatam.equlatam_backv2.guias.dto.request.GuiaUpdateRequest;
import com.equalatam.equlatam_backv2.guias.entity.Guia;
import com.equalatam.equlatam_backv2.guias.service.GuiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/guias")
@RequiredArgsConstructor
public class GuiaController {

    private final GuiaService guiaService;

    @PostMapping
    public Guia crear(@RequestBody GuiaCreateRequest request) {
        return guiaService.crear(request);
    }

    @PutMapping("/{id}")
    public Guia actualizar(@PathVariable UUID id,
                           @RequestBody GuiaUpdateRequest request) {
        return guiaService.actualizar(id, request);
    }

    @PutMapping("/{id}/estado")
    public Guia cambiarEstado(@PathVariable UUID id,
                              @RequestBody GuiaEstadoRequest request) {
        return guiaService.cambiarEstado(id, request);
    }

    @GetMapping
    public List<Guia> listar() {
        return guiaService.listar();
    }

    @GetMapping("/{id}")
    public Guia obtener(@PathVariable UUID id) {
        return guiaService.obtenerPorId(id);
    }

    @GetMapping("/estado/{estado}")
    public List<Guia> porEstado(@PathVariable Enums.EstadoGuia estado) {
        return guiaService.porEstado(estado);
    }
}
