package com.equalatam.equlatam_backv2.financiero.repository;

import com.equalatam.equlatam_backv2.financiero.entity.Cotizacion;
import com.equalatam.equlatam_backv2.financiero.enums.EstadoCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CotizacionRepository extends JpaRepository<Cotizacion, UUID> {

    List<Cotizacion> findByClienteIdOrderByCreadoEnDesc(UUID clienteId);

    List<Cotizacion> findByPedidoId(UUID pedidoId);

    Optional<Cotizacion> findByNumeroCotizacion(String numeroCotizacion);

    List<Cotizacion> findByEstado(EstadoCotizacion estado);

    // Cotizaciones pendientes de un cliente
    List<Cotizacion> findByClienteIdAndEstado(UUID clienteId, EstadoCotizacion estado);

    // Último número para autoincremental
    @Query("SELECT MAX(c.numeroCotizacion) FROM Cotizacion c WHERE c.numeroCotizacion LIKE :prefijo%")
    Optional<String> findUltimoNumero(@Param("prefijo") String prefijo);
}