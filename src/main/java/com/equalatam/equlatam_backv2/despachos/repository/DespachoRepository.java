package com.equalatam.equlatam_backv2.despachos.repository;

import com.equalatam.equlatam_backv2.despachos.entity.Despacho;
import com.equalatam.equlatam_backv2.despachos.entity.EstadoDespacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DespachoRepository extends JpaRepository<Despacho, UUID> {

    Optional<Despacho> findByNumeroDespacho(String numeroDespacho);

    boolean existsByNumeroDespacho(String numeroDespacho);

    List<Despacho> findByEstado(EstadoDespacho estado);

    List<Despacho> findBySucursalOrigenId(UUID sucursalId);

    List<Despacho> findBySucursalDestinoId(UUID sucursalId);

    List<Despacho> findBySucursalOrigenIdAndEstado(UUID sucursalId, EstadoDespacho estado);

    List<Despacho> findBySucursalDestinoIdAndEstado(UUID sucursalId, EstadoDespacho estado);

    // Despachos abiertos en una sucursal origen (para agregar pedidos)
    @Query("SELECT d FROM Despacho d WHERE d.sucursalOrigen.id = :sucursalId " +
            "AND d.estado = 'ABIERTO'")
    List<Despacho> findAbiertosEnSucursal(@Param("sucursalId") UUID sucursalId);

    // Despachos en tránsito hacia una sucursal destino
    @Query("SELECT d FROM Despacho d WHERE d.sucursalDestino.id = :sucursalId " +
            "AND d.estado = 'EN_TRANSITO'")
    List<Despacho> findEnTransitoHacia(@Param("sucursalId") UUID sucursalId);

    // Verificar si un pedido ya está en algún despacho activo
    @Query("SELECT COUNT(dd) > 0 FROM DespachoDetalle dd WHERE dd.pedido.id = :pedidoId " +
            "AND dd.despacho.estado NOT IN ('PROCESADO', 'CANCELADO')")
    boolean pedidoYaEnDespachoActivo(@Param("pedidoId") UUID pedidoId);
}