package com.equalatam.equlatam_backv2.despacho.controller;

import com.equalatam.equlatam_backv2.despacho.dto.request.AsignarGuiaDespachoRequest;
import com.equalatam.equlatam_backv2.despacho.dto.request.DespachoCreateRequest;
import com.equalatam.equlatam_backv2.despacho.entity.Despacho;
import com.equalatam.equlatam_backv2.despacho.service.DespachoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/despachos")
@RequiredArgsConstructor
public class DespachoController {

    private final DespachoService despachoService;

    @PostMapping
    public Despacho crear(@RequestBody DespachoCreateRequest request) {
        return despachoService.crear(
                request.sucursalId(),
                request.rutaId()
        );
    }

    @PostMapping("/{id}/guias")
    public void asignarGuia(@PathVariable UUID id,
                            @RequestBody AsignarGuiaDespachoRequest request) {
        despachoService.asignarGuia(id, request.guiaId());
    }

    @PutMapping("/{id}/iniciar")
    public Despacho iniciar(@PathVariable UUID id) {
        return despachoService.iniciar(id);
    }

    @PutMapping("/{id}/cerrar")
    public Despacho cerrar(@PathVariable UUID id) {
        return despachoService.cerrar(id);
    }

    @GetMapping("/sucursal/{sucursalId}")
    public List<Despacho> porSucursal(@PathVariable UUID sucursalId) {
        return despachoService.listarPorSucursal(sucursalId);
    }
}
