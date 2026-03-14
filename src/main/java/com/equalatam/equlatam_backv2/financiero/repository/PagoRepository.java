// ─── PagoRepository.java ─────────────────────────────────────────────────────
package com.equalatam.equlatam_backv2.financiero.repository;

import com.equalatam.equlatam_backv2.financiero.entity.Pago;
import com.equalatam.equlatam_backv2.financiero.enums.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PagoRepository extends JpaRepository<Pago, UUID> {

    List<Pago> findByFacturaId(UUID facturaId);

    List<Pago> findByClienteIdOrderByCreadoEnDesc(UUID clienteId);

    Optional<Pago> findByNumeroPago(String numeroPago);

    List<Pago> findByEstado(EstadoPago estado);

    // Total pagado confirmado de una factura
    @Query("""
        SELECT COALESCE(SUM(p.monto), 0) FROM Pago p
        WHERE p.factura.id = :facturaId AND p.estado = 'CONFIRMADO'
        """)
    Double sumPagadoFactura(@Param("facturaId") UUID facturaId);

    // Último número para autoincremental
    @Query("SELECT MAX(p.numeroPago) FROM Pago p WHERE p.numeroPago LIKE :prefijo%")
    Optional<String> findUltimoNumero(@Param("prefijo") String prefijo);
}