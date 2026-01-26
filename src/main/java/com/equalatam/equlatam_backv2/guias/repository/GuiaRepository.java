package com.equalatam.equlatam_backv2.guias.repository;

import com.equalatam.equlatam_backv2.guias.Enums;
import com.equalatam.equlatam_backv2.guias.entity.Guia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GuiaRepository extends JpaRepository<Guia, UUID> {

    Optional<Guia> findByNumeroGuia(String numeroGuia);

    List<Guia> findByEstado(Enums.EstadoGuia estado);

    List<Guia> findByClienteId(UUID clienteId);

    List<Guia> findByBeneficiarioId(UUID beneficiarioId);
}