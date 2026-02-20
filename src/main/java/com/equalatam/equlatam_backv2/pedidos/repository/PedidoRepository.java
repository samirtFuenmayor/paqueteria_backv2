package com.equalatam.equlatam_backv2.pedidos.repository;

import com.equalatam.equlatam_backv2.pedidos.entity.EstadoPedido;
import com.equalatam.equlatam_backv2.pedidos.entity.Pedido;
import com.equalatam.equlatam_backv2.pedidos.entity.TipoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    boolean existsByNumeroPedido(String numeroPedido);

    boolean existsByTrackingExterno(String trackingExterno);

    // Por cliente
    List<Pedido> findByClienteId(UUID clienteId);
    List<Pedido> findByClienteIdAndEstado(UUID clienteId, EstadoPedido estado);

    // Por estado
    List<Pedido> findByEstado(EstadoPedido estado);

    // Por sucursal origen
    List<Pedido> findBySucursalOrigenId(UUID sucursalId);
    List<Pedido> findBySucursalOrigenIdAndEstado(UUID sucursalId, EstadoPedido estado);

    // Por sucursal destino
    List<Pedido> findBySucursalDestinoId(UUID sucursalId);
    List<Pedido> findBySucursalDestinoIdAndEstado(UUID sucursalId, EstadoPedido estado);

    // Por tipo
    List<Pedido> findByTipo(TipoPedido tipo);

    // Pedidos pendientes de recibir en una sede exterior
    @Query("SELECT p FROM Pedido p WHERE p.sucursalOrigen.id = :sucursalId " +
            "AND p.estado = 'REGISTRADO'")
    List<Pedido> findPendientesEnSede(@Param("sucursalId") UUID sucursalId);

    // Pedidos listos para despachar (agrupable en despacho)
    @Query("SELECT p FROM Pedido p WHERE p.sucursalOrigen.id = :sucursalId " +
            "AND p.estado = 'RECIBIDO_EN_SEDE'")
    List<Pedido> findListosParaDespachar(@Param("sucursalId") UUID sucursalId);

    // Pedidos disponibles para retiro en sucursal destino
    @Query("SELECT p FROM Pedido p WHERE p.sucursalDestino.id = :sucursalId " +
            "AND p.estado = 'DISPONIBLE_EN_SUCURSAL'")
    List<Pedido> findDisponiblesEnSucursal(@Param("sucursalId") UUID sucursalId);

    // Buscador general
    @Query("SELECT p FROM Pedido p WHERE " +
            "LOWER(p.numeroPedido) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(p.trackingExterno) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(p.cliente.nombres) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(p.cliente.apellidos) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "p.cliente.casillero LIKE CONCAT('%', :q, '%')")
    List<Pedido> buscar(@Param("q") String query);

    // Conteo por estado para dashboard
    long countByEstado(EstadoPedido estado);

    // Pedidos en un rango de fechas
    List<Pedido> findByFechaRegistroBetween(LocalDateTime desde, LocalDateTime hasta);
}