package com.equalatam.equlatam_backv2.pedidos.repository;

import com.equalatam.equlatam_backv2.pedidos.entity.PedidoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, UUID> {
    List<PedidoItem> findByPedidoId(UUID pedidoId);
    List<PedidoItem> findByPedidoIdAndLlegoTrue(UUID pedidoId);
    List<PedidoItem> findByPedidoIdAndLlegoFalse(UUID pedidoId);
    // Borrado masivo por pedido (usado al cancelar cotización)
    void deleteAll(List<PedidoItem> items); // ya hereda de JpaRepository

    // Si quieres borrado directo sin cargar entidades:
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(
            "DELETE FROM PedidoItem i WHERE i.pedido.id = :pedidoId")
    void deleteByPedidoId(@Param("pedidoId") UUID pedidoId);
}
