package com.equalatam.equlatam_backv2.despachos.repository;

import com.equalatam.equlatam_backv2.despachos.entity.DespachoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DespachoDetalleRepository extends JpaRepository<DespachoDetalle, UUID> {

    List<DespachoDetalle> findByDespachoId(UUID despachoId);

    Optional<DespachoDetalle> findByDespachoIdAndPedidoId(UUID despachoId, UUID pedidoId);

    boolean existsByDespachoIdAndPedidoId(UUID despachoId, UUID pedidoId);
}
