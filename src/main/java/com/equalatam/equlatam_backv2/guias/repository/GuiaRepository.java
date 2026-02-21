package com.equalatam.equlatam_backv2.guias.repository;

import com.equalatam.equlatam_backv2.guias.entity.EstadoGuia;
import com.equalatam.equlatam_backv2.guias.entity.Guia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GuiaRepository extends JpaRepository<Guia, UUID> {

    Optional<Guia> findByNumeroGuia(String numeroGuia);
    boolean existsByNumeroGuia(String numeroGuia);
    boolean existsByPedidoId(UUID pedidoId);

    Optional<Guia> findByPedidoId(UUID pedidoId);

    List<Guia> findByEstado(EstadoGuia estado);
    List<Guia> findByDestinatarioId(UUID clienteId);
    List<Guia> findByNumeroDespacho(String numeroDespacho);
    List<Guia> findBySucursalOrigenId(UUID sucursalId);
    List<Guia> findBySucursalDestinoId(UUID sucursalId);
}
