package com.equalatam.equlatam_backv2.despacho.service;

import com.equalatam.equlatam_backv2.despacho.EnumsDespacho;
import com.equalatam.equlatam_backv2.despacho.entity.Despacho;
import com.equalatam.equlatam_backv2.despacho.entity.DespachoGuia;
import com.equalatam.equlatam_backv2.despacho.repository.DespachoGuiaRepository;
import com.equalatam.equlatam_backv2.despacho.repository.DespachoRepository;
import com.equalatam.equlatam_backv2.guias.Enums;
import com.equalatam.equlatam_backv2.guias.entity.Guia;
import com.equalatam.equlatam_backv2.guias.repository.GuiaRepository;
import com.equalatam.equlatam_backv2.rutas.entity.Ruta;
import com.equalatam.equlatam_backv2.rutas.repository.RutaRepository;
import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import com.equalatam.equlatam_backv2.sucursales.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DespachoService {

    private final DespachoRepository despachoRepository;
    private final DespachoGuiaRepository despachoGuiaRepository;
    private final RutaRepository rutaRepository;
    private final GuiaRepository guiaRepository;
    private final SucursalRepository sucursalRepository;

    public Despacho crear(UUID sucursalId, UUID rutaId) {

        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new RuntimeException("Sucursal no existe"));

        Ruta ruta = rutaRepository.findById(rutaId)
                .orElseThrow(() -> new RuntimeException("Ruta no existe"));

        Despacho despacho = new Despacho();
        despacho.setSucursal(sucursal);
        despacho.setRuta(ruta);
        despacho.setEstado(EnumsDespacho.EstadoDespacho.CREADO);
        despacho.setFechaCreacion(LocalDateTime.now());

        return despachoRepository.save(despacho);
    }

    public void asignarGuia(UUID despachoId, UUID guiaId) {

        if (despachoGuiaRepository.existsByGuiaId(guiaId)) {
            throw new RuntimeException("Guía ya está en otro despacho");
        }

        Despacho despacho = obtener(despachoId);

        Guia guia = guiaRepository.findById(guiaId)
                .orElseThrow(() -> new RuntimeException("Guía no existe"));

        DespachoGuia dg = new DespachoGuia();
        dg.setDespacho(despacho);
        dg.setGuia(guia);

        despachoGuiaRepository.save(dg);
    }

    public Despacho iniciar(UUID despachoId) {
        Despacho despacho = obtener(despachoId);
        despacho.setEstado(EnumsDespacho.EstadoDespacho.EN_RUTA);
        despacho.setFechaSalida(LocalDateTime.now());
        return despachoRepository.save(despacho);
    }

    public Despacho cerrar(UUID despachoId) {
        Despacho despacho = obtener(despachoId);
        despacho.setEstado(EnumsDespacho.EstadoDespacho.CERRADO);
        despacho.setFechaCierre(LocalDateTime.now());
        return despachoRepository.save(despacho);
    }

    public Despacho obtener(UUID id) {
        return despachoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despacho no encontrado"));
    }

    public List<Despacho> listarPorSucursal(UUID sucursalId) {
        return despachoRepository.findBySucursalId(sucursalId);
    }
}


