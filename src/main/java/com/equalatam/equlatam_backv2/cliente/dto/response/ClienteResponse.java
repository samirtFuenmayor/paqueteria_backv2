package com.equalatam.equlatam_backv2.cliente.dto.response;

import com.equalatam.equlatam_backv2.cliente.entity.Cliente;
import com.equalatam.equlatam_backv2.cliente.entity.EstadoCliente;
import com.equalatam.equlatam_backv2.cliente.entity.TipoIdentificacion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ClienteResponse(
        UUID id,
        TipoIdentificacion tipoIdentificacion,
        String numeroIdentificacion,
        String nombres,
        String apellidos,
        String email,
        String telefono,
        LocalDate fechaNacimiento,
        String pais,
        String provincia,
        String ciudad,
        String direccion,
        String casillero,
        UUID sucursalId,
        String sucursalNombre,
        String sucursalPais,
        EstadoCliente estado,
        String observaciones,
        LocalDateTime creadoEn
) {
    public static ClienteResponse from(Cliente c) {
        return new ClienteResponse(
                c.getId(),
                c.getTipoIdentificacion(),
                c.getNumeroIdentificacion(),
                c.getNombres(),
                c.getApellidos(),
                c.getEmail(),
                c.getTelefono(),
                c.getFechaNacimiento(),
                c.getPais(),
                c.getProvincia(),
                c.getCiudad(),
                c.getDireccion(),
                c.getCasillero(),
                c.getSucursalAsignada() != null ? c.getSucursalAsignada().getId() : null,
                c.getSucursalAsignada() != null ? c.getSucursalAsignada().getNombre() : null,
                c.getSucursalAsignada() != null ? c.getSucursalAsignada().getPais() : null,
                c.getEstado(),
                c.getObservaciones(),
                c.getCreadoEn()
        );
    }
}