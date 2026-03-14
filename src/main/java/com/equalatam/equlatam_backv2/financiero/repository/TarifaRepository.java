// ─── TarifaRepository.java ───────────────────────────────────────────────────
package com.equalatam.equlatam_backv2.financiero.repository;

import com.equalatam.equlatam_backv2.financiero.entity.Tarifa;
import com.equalatam.equlatam_backv2.financiero.enums.CategoriaPaquete;
import com.equalatam.equlatam_backv2.pedidos.entity.TipoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarifaRepository extends JpaRepository<Tarifa, UUID> {

    List<Tarifa> findByActivoTrue();

    List<Tarifa> findByCategoria(CategoriaPaquete categoria);

    List<Tarifa> findByTipoPedidoAndActivoTrue(TipoPedido tipoPedido);

    List<Tarifa> findByCategoriaAndTipoPedidoAndActivoTrue(
            CategoriaPaquete categoria, TipoPedido tipoPedido);

    // Busca la tarifa vigente para una categoría, tipo y peso dado
    @Query("""
        SELECT t FROM Tarifa t
        WHERE t.activo = true
          AND t.categoria = :categoria
          AND t.tipoPedido = :tipoPedido
          AND (t.pesoDesde IS NULL OR t.pesoDesde <= :peso)
          AND (t.pesoHasta IS NULL OR t.pesoHasta >= :peso)
          AND (t.vigenciaDesde IS NULL OR t.vigenciaDesde <= :hoy)
          AND (t.vigenciaHasta IS NULL OR t.vigenciaHasta >= :hoy)
        ORDER BY t.precioBase ASC
        LIMIT 1
        """)
    Optional<Tarifa> findTarifaAplicable(
            @Param("categoria")  CategoriaPaquete categoria,
            @Param("tipoPedido") TipoPedido tipoPedido,
            @Param("peso")       Double peso,
            @Param("hoy")        LocalDate hoy);
}