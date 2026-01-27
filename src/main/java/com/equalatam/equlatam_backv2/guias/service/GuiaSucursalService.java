package com.equalatam.equlatam_backv2.guias.service;

import com.equalatam.equlatam_backv2.guias.Enums;
import com.equalatam.equlatam_backv2.guias.entity.Guia;
import com.equalatam.equlatam_backv2.guias.entity.GuiaSucursal;
import com.equalatam.equlatam_backv2.guias.repository.GuiaRepository;
import com.equalatam.equlatam_backv2.guias.repository.GuiaSucursalRepository;
import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import com.equalatam.equlatam_backv2.sucursales.repository.SucursalRepository;
import com.equalatam.equlatam_backv2.tracking.service.TrackingAutomaticoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GuiaSucursalService {

    private final GuiaRepository guiaRepository;
    private final SucursalRepository sucursalRepository;
    private final GuiaSucursalRepository guiaSucursalRepository;
    private final TrackingAutomaticoService trackingService;

    public void ingresarAGuiaASucursal(UUID guiaId, UUID sucursalId) {

        if (guiaSucursalRepository.existsByGuiaIdAndFechaSalidaIsNull(guiaId)) {
            throw new RuntimeException("La guía ya está en una sucursal");
        }

        Guia guia = guiaRepository.findById(guiaId)
                .orElseThrow(() -> new RuntimeException("Guía no existe"));

        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new RuntimeException("Sucursal no existe"));

        GuiaSucursal gs = new GuiaSucursal();
        gs.setGuia(guia);
        gs.setSucursal(sucursal);
        gs.setFechaIngreso(LocalDateTime.now());

        guia.setEstado(Enums.EstadoGuia.EN_SUCURSAL);
        guiaRepository.save(guia);

        // 🔥 TRACKING AUTOMÁTICO AQUÍ
        trackingService.registrarEvento(
                guia,
                Enums.EstadoGuia.EN_SUCURSAL,
                "INGRESO A SUCURSAL",
                "Guía ingresó a la sucursal " + sucursal.getNombre()
        );

        guiaSucursalRepository.save(gs);
    }
}
