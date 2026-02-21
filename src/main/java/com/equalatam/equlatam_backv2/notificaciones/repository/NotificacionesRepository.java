package com.equalatam.equlatam_backv2.notificaciones.repository;

import com.equalatam.equlatam_backv2.notificaciones.entity.Notificaciones;
import com.equalatam.equlatam_backv2.notificaciones.entity.TipoNotificaciones;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificacionesRepository extends JpaRepository<Notificaciones, UUID> {

    List<Notificaciones> findByClienteIdOrderByCreadoEnDesc(UUID clienteId);
    List<Notificaciones> findByPedidoIdOrderByCreadoEnDesc(UUID pedidoId);
    List<Notificaciones> findByEnviadoFalse();
    List<Notificaciones> findByTipo(TipoNotificaciones tipo);
    long countByClienteIdAndEnviadoTrue(UUID clienteId);
}
