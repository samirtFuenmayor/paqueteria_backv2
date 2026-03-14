// ─── FacturaRepository.java ───────────────────────────────────────────────────
package com.equalatam.equlatam_backv2.financiero.repository;

import com.equalatam.equlatam_backv2.financiero.entity.Factura;
import com.equalatam.equlatam_backv2.financiero.enums.EstadoFactura;
import com.equalatam.equlatam_backv2.financiero.enums.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FacturaRepository extends JpaRepository<Factura, UUID> {

    Optional<Factura> findByNumeroFactura(String numeroFactura);

    List<Factura> findByClienteIdOrderByCreadoEnDesc(UUID clienteId);

    List<Factura> findByPedidoId(UUID pedidoId);

    List<Factura> findByEstado(EstadoFactura estado);

    List<Factura> findByTipoDocumento(TipoDocumento tipo);

    // Facturas emitidas en un rango de fechas (para reportes)
    @Query("""
        SELECT f FROM Factura f
        WHERE f.fechaEmision BETWEEN :desde AND :hasta
        ORDER BY f.fechaEmision DESC
        """)
    List<Factura> findByRangoFechas(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    // Último secuencial para un establecimiento+punto
    @Query("""
        SELECT MAX(f.secuencial) FROM Factura f
        WHERE f.establecimiento = :est AND f.puntoEmision = :punto
          AND f.tipoDocumento = :tipo
        """)
    Optional<Long> findUltimoSecuencial(
            @Param("est")   String est,
            @Param("punto") String punto,
            @Param("tipo")  TipoDocumento tipo);

    // Resumen financiero por cliente
    @Query("""
        SELECT COALESCE(SUM(f.total), 0) FROM Factura f
        WHERE f.cliente.id = :clienteId
          AND f.estado IN ('EMITIDA', 'VENCIDA')
        """)
    Double sumDeudaCliente(@Param("clienteId") UUID clienteId);

    // Facturas vencidas (para cron job)
    @Query("""
        SELECT f FROM Factura f
        WHERE f.estado = 'EMITIDA'
          AND f.fechaVencimiento < :hoy
        """)
    List<Factura> findVencidas(@Param("hoy") LocalDate hoy);
}