package com.equalatam.equlatam_backv2.cliente.service;

import com.equalatam.equlatam_backv2.cliente.Enums;
import com.equalatam.equlatam_backv2.cliente.dto.request.ClienteCreateRequest;
import com.equalatam.equlatam_backv2.cliente.entity.Cliente;
import com.equalatam.equlatam_backv2.cliente.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repo;

    public Cliente create(ClienteCreateRequest r) {
        Cliente c = new Cliente();
        c.setIdentificacion(r.identificacion());
        c.setNombres(r.nombres());
        c.setApellidos(r.apellidos());
        c.setEmail(r.email());
        c.setTelefono(r.telefono());
        c.setCallePrincipal(r.callePrincipal());
        c.setCalleSecundaria(r.calleSecundaria());
        c.setNacionalidad(r.nacionalidad());
        c.setProvincia(r.provincia());
        c.setCiudad(r.ciudad());
        c.setFechaNacimiento(r.fechaNacimiento());
        return repo.save(c);
    }

    public Cliente findByIdentificacion(String identificacion) {
        return repo.findByIdentificacion(identificacion)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    public Cliente cambiarEstado(UUID id, Enums.EstadoPersona estado) {
        Cliente c = repo.findById(id).orElseThrow();
        c.setEstado(estado);
        return repo.save(c);
    }
}
