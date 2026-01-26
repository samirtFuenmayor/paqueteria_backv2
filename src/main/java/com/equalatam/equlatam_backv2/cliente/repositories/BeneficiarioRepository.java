package com.equalatam.equlatam_backv2.cliente.repositories;

import com.equalatam.equlatam_backv2.cliente.entity.Beneficiario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeneficiarioRepository extends JpaRepository<Beneficiario, UUID> {
}
