package com.equalatam.equlatam_backv2.financiero.repository;

import com.equalatam.equlatam_backv2.financiero.entity.NotaAjuste;
import com.equalatam.equlatam_backv2.financiero.enums.TipoNota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotaAjusteRepository extends JpaRepository<NotaAjuste, UUID> {

    List<NotaAjuste> findByFacturaOrigenId(UUID facturaId);

    List<NotaAjuste> findByTipo(TipoNota tipo);

    Optional<NotaAjuste> findByNumeroNota(String numeroNota);

    @Query("""
        SELECT MAX(n.secuencial) FROM NotaAjuste n
        WHERE n.establecimiento = :est AND n.puntoEmision = :punto
          AND n.tipo = :tipo
        """)
    Optional<Long> findUltimoSecuencial(
            @Param("est")   String est,
            @Param("punto") String punto,
            @Param("tipo")  TipoNota tipo);
}