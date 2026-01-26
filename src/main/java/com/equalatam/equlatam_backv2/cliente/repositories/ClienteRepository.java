package com.equalatam.equlatam_backv2.cliente.repositories;

import com.equalatam.equlatam_backv2.cliente.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
    Optional<Cliente> findByIdentificacion(String identificacion);
}
