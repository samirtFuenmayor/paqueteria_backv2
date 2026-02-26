package com.equalatam.equlatam_backv2.cliente.repositories;

import com.equalatam.equlatam_backv2.cliente.entity.Cliente;
import com.equalatam.equlatam_backv2.cliente.entity.EstadoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    Optional<Cliente> findByNumeroIdentificacion(String numeroIdentificacion);
    Optional<Cliente> findByCasillero(String casillero);

    boolean existsByNumeroIdentificacion(String numeroIdentificacion);
    boolean existsByEmail(String email);
    boolean existsByCasillero(String casillero);

    List<Cliente> findByEstado(EstadoCliente estado);
    List<Cliente> findBySucursalAsignadaId(UUID sucursalId);
    List<Cliente> findBySucursalAsignadaIdAndEstado(UUID sucursalId, EstadoCliente estado);

    // ─── Afiliados de un titular ──────────────────────────────────────────────
    @Query("SELECT c FROM Cliente c WHERE c.titular.id = :titularId")
    List<Cliente> findByTitularId(@Param("titularId") UUID titularId);

    @Query("SELECT c FROM Cliente c WHERE " +
            "LOWER(c.nombres) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(c.apellidos) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "c.numeroIdentificacion LIKE CONCAT('%', :q, '%') OR " +
            "c.casillero LIKE CONCAT('%', :q, '%')")
    List<Cliente> buscar(@Param("q") String query);
}