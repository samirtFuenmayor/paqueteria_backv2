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

    // Buscar por número de identificación (login del cliente)
    Optional<Cliente> findByNumeroIdentificacion(String numeroIdentificacion);

    // Buscar por casillero
    Optional<Cliente> findByCasillero(String casillero);

    // Verificar duplicados
    boolean existsByNumeroIdentificacion(String numeroIdentificacion);
    boolean existsByEmail(String email);
    boolean existsByCasillero(String casillero);

    // Listar por estado
    List<Cliente> findByEstado(EstadoCliente estado);

    // Listar por sucursal asignada
    List<Cliente> findBySucursalAsignadaId(UUID sucursalId);

    // Listar activos por sucursal
    List<Cliente> findBySucursalAsignadaIdAndEstado(UUID sucursalId, EstadoCliente estado);

    // Búsqueda por nombre o apellido o identificación (para buscador)
    @Query("SELECT c FROM Cliente c WHERE " +
            "LOWER(c.nombres) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(c.apellidos) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "c.numeroIdentificacion LIKE CONCAT('%', :q, '%') OR " +
            "c.casillero LIKE CONCAT('%', :q, '%')")
    List<Cliente> buscar(@Param("q") String query);
}
