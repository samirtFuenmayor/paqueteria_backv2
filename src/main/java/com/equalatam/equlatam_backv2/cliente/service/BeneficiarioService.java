package com.equalatam.equlatam_backv2.cliente.service;

import com.equalatam.equlatam_backv2.cliente.Enums;
import com.equalatam.equlatam_backv2.cliente.dto.request.BeneficiarioCreateRequest;
import com.equalatam.equlatam_backv2.cliente.entity.Beneficiario;
import com.equalatam.equlatam_backv2.cliente.entity.Cliente;
import com.equalatam.equlatam_backv2.cliente.repositories.BeneficiarioRepository;
import com.equalatam.equlatam_backv2.cliente.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BeneficiarioService {

    private final ClienteRepository clienteRepo;
    private final BeneficiarioRepository beneficiarioRepo;

    public Beneficiario addToTitular(
            String identificacionTitular,
            BeneficiarioCreateRequest r
    ) {
        Cliente titular = clienteRepo
                .findByIdentificacion(identificacionTitular)
                .orElseThrow(() -> new RuntimeException("Titular no existe"));

        Beneficiario b = new Beneficiario();
        b.setIdentificacion(r.identificacion());
        b.setNombres(r.nombres());
        b.setApellidos(r.apellidos());
        b.setEmail(r.email());
        b.setTelefono(r.telefono());
        b.setTipo(r.tipo());
        b.setTitular(titular);

        return beneficiarioRepo.save(b);
    }

    public Beneficiario cambiarEstado(UUID id, Enums.EstadoPersona estado) {
        Beneficiario b = beneficiarioRepo.findById(id).orElseThrow();
        b.setEstado(estado);
        return beneficiarioRepo.save(b);
    }
}
