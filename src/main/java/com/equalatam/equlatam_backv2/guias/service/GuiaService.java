package com.equalatam.equlatam_backv2.guias.service;

import com.equalatam.equlatam_backv2.cliente.entity.Beneficiario;
import com.equalatam.equlatam_backv2.cliente.entity.Cliente;
import com.equalatam.equlatam_backv2.cliente.repositories.BeneficiarioRepository;
import com.equalatam.equlatam_backv2.cliente.repositories.ClienteRepository;
import com.equalatam.equlatam_backv2.guias.Enums;
import com.equalatam.equlatam_backv2.guias.dto.request.GuiaCreateRequest;
import com.equalatam.equlatam_backv2.guias.dto.request.GuiaEstadoRequest;
import com.equalatam.equlatam_backv2.guias.dto.request.GuiaUpdateRequest;
import com.equalatam.equlatam_backv2.guias.entity.Guia;
import com.equalatam.equlatam_backv2.guias.repository.GuiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GuiaService {

    private final GuiaRepository guiaRepository;
    private final ClienteRepository clienteRepository;
    private final BeneficiarioRepository beneficiarioRepository;

    public Guia crear(GuiaCreateRequest request) {

        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no existe"));

        Beneficiario beneficiario = null;
        if (request.beneficiarioId() != null) {
            beneficiario = beneficiarioRepository.findById(request.beneficiarioId())
                    .orElseThrow(() -> new RuntimeException("Beneficiario no existe"));
        }

        Guia guia = new Guia();
        guia.setNumeroGuia(UUID.randomUUID().toString());
        guia.setCliente(cliente);
        guia.setBeneficiario(beneficiario);
        guia.setDescripcion(request.descripcion());
        guia.setPeso(request.peso());
        guia.setValorDeclarado(request.valorDeclarado());
        guia.setEstado(Enums.EstadoGuia.REGISTRADA);
        guia.setFechaCreacion(LocalDateTime.now());

        return guiaRepository.save(guia);
    }

    public Guia actualizar(UUID id, GuiaUpdateRequest request) {
        Guia guia = obtenerPorId(id);
        guia.setDescripcion(request.descripcion());
        guia.setPeso(request.peso());
        guia.setValorDeclarado(request.valorDeclarado());
        guia.setFechaActualizacion(LocalDateTime.now());
        return guiaRepository.save(guia);
    }

    public Guia cambiarEstado(UUID id, GuiaEstadoRequest request) {
        Guia guia = obtenerPorId(id);
        guia.setEstado(request.estado());
        return guiaRepository.save(guia);
    }

    public Guia obtenerPorId(UUID id) {
        return guiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guía no encontrada"));
    }

    public List<Guia> listar() {
        return guiaRepository.findAll();
    }

    public List<Guia> porEstado(Enums.EstadoGuia estado) {
        return guiaRepository.findByEstado(estado);
    }
}
