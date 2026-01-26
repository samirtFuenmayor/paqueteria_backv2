package com.equalatam.equlatam_backv2.tracking.controller;

import com.equalatam.equlatam_backv2.tracking.dto.request.TrackingInternoCreateRequest;
import com.equalatam.equlatam_backv2.tracking.dto.request.TrackingInternoUpdateRequest;
import com.equalatam.equlatam_backv2.tracking.dto.request.response.TrackingInternoResponse;
import com.equalatam.equlatam_backv2.tracking.service.TrackingInternoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tracking-interno")
@RequiredArgsConstructor
public class TrackingInternoController {

    private final TrackingInternoService service;

    @PostMapping
    public TrackingInternoResponse crear(
            @RequestBody TrackingInternoCreateRequest request) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    public TrackingInternoResponse actualizar(
            @PathVariable UUID id,
            @RequestBody TrackingInternoUpdateRequest request) {
        return service.actualizar(id, request);
    }

    @GetMapping("/guia/{guiaId}")
    public List<TrackingInternoResponse> listarPorGuia(
            @PathVariable UUID guiaId) {
        return service.listarPorGuia(guiaId);
    }

    @GetMapping
    public List<TrackingInternoResponse> listarTodos() {
        return service.listarTodos();
    }
}
