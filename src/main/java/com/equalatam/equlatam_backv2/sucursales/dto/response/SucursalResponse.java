package com.equalatam.equlatam_backv2.sucursales.dto.response;

import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import com.equalatam.equlatam_backv2.sucursales.entity.TipoSucursal;

import java.time.LocalDateTime;
import java.util.UUID;

public record SucursalResponse(
        UUID id,
        String nombre,
        String codigo,
        TipoSucursal tipo,
        String pais,
        String ciudad,
        String direccion,
        String telefono,
        String email,
        String responsable,
        String prefijoCasillero,
        boolean activa,
        LocalDateTime creadoEn
) {
    public static SucursalResponse from(Sucursal s) {
        return new SucursalResponse(
                s.getId(),
                s.getNombre(),
                s.getCodigo(),
                s.getTipo(),
                s.getPais(),
                s.getCiudad(),
                s.getDireccion(),
                s.getTelefono(),
                s.getEmail(),
                s.getResponsable(),
                s.getPrefijoCasillero(),
                s.isActiva(),
                s.getCreadoEn()
        );
    }
}
