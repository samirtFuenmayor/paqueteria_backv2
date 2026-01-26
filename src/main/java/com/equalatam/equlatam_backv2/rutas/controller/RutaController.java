package com.equalatam.equlatam_backv2.rutas.controller;


import com.equalatam.equlatam_backv2.rutas.Enums;
import com.equalatam.equlatam_backv2.rutas.entity.Ruta;
import com.equalatam.equlatam_backv2.rutas.entity.dto.request.AsignarRutaGuiaRequest;
import com.equalatam.equlatam_backv2.rutas.entity.dto.request.RutaCreateRequest;
import com.equalatam.equlatam_backv2.rutas.entity.dto.request.RutaEstadoRequest;
import com.equalatam.equlatam_backv2.rutas.entity.dto.request.RutaUpdateRequest;
import com.equalatam.equlatam_backv2.rutas.service.RutaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
public class RutaController {

    private final RutaService rutaService;

    @PostMapping
    public Ruta crear(@RequestBody RutaCreateRequest request) {
        return rutaService.crear(request);
    }

    @PutMapping("/{id}")
    public Ruta actualizar(@PathVariable UUID id,
                           @RequestBody RutaUpdateRequest request) {
        return rutaService.actualizar(id, request);
    }

    @PutMapping("/{id}/estado")
    public Ruta cambiarEstado(@PathVariable UUID id,
                              @RequestBody RutaEstadoRequest request) {
        return rutaService.cambiarEstado(id, request);
    }

    @PutMapping("/asignar/{guiaId}")
    public Ruta asignarRuta(
            @PathVariable UUID guiaId,
            @RequestBody AsignarRutaGuiaRequest request) {

        return rutaService.asignarRutaAGuia(guiaId, request.rutaId());
    }

    @GetMapping
    public List<Ruta> listar() {
        return rutaService.listar();
    }

    @GetMapping("/{id}")
    public Ruta obtener(@PathVariable UUID id) {
        return rutaService.obtenerPorId(id);
    }

    @GetMapping("/tipo/{tipo}")
    public List<Ruta> porTipo(@PathVariable Enums.TipoRuta tipo) {
        return rutaService.porTipo(tipo);
    }

    @GetMapping("/estado/{estado}")
    public List<Ruta> porEstado(@PathVariable Enums.EstadoRuta estado) {
        return rutaService.porEstado(estado);
    }
}