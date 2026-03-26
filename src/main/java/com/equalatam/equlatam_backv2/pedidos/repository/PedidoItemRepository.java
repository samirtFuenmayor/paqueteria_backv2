package com.equalatam.equlatam_backv2.pedidos.repository;

import com.equalatam.equlatam_backv2.pedidos.entity.PedidoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, UUID> {
    List<PedidoItem> findByPedidoId(UUID pedidoId);
    List<PedidoItem> findByPedidoIdAndLlegoTrue(UUID pedidoId);
    List<PedidoItem> findByPedidoIdAndLlegoFalse(UUID pedidoId);
}
