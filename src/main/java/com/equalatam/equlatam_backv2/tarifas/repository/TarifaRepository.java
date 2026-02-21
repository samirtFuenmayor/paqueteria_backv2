package com.equalatam.equlatam_backv2.tarifas.repository;

import com.equalatam.equlatam_backv2.tarifas.entity.Tarifa;
import com.equalatam.equlatam_backv2.tarifas.entity.TipoTarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TarifaRepository extends JpaRepository<Tarifa, UUID> {

    List<Tarifa> findByActivaTrue();
    List<Tarifa> findByTipo(TipoTarifa tipo);
    List<Tarifa> findByTipoAndActivaTrue(TipoTarifa tipo);
    List<Tarifa> findBySucursalOrigenId(UUID sucursalId);

    // Tarifas activas para una sucursal origen específica o globales
    @Query("SELECT t FROM Tarifa t WHERE t.activa = true AND " +
            "(t.sucursalOrigen.id = :sucursalId OR t.sucursalOrigen IS NULL)")
    List<Tarifa> findActivasBySucursal(@Param("sucursalId") UUID sucursalId);

    // Tarifa por libra activa para una sucursal
    @Query("SELECT t FROM Tarifa t WHERE t.activa = true AND t.tipo = 'POR_LIBRA' AND " +
            "(t.sucursalOrigen.id = :sucursalId OR t.sucursalOrigen IS NULL) " +
            "ORDER BY t.sucursalOrigen.id NULLS LAST LIMIT 1")
    Tarifa findTarifaPorLibra(@Param("sucursalId") UUID sucursalId);
}
