package com.equalatam.equlatam_backv2.guias.repository;

import com.equalatam.equlatam_backv2.guias.entity.GuiaSucursal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GuiaSucursalRepository extends JpaRepository<GuiaSucursal, UUID> {

    List<GuiaSucursal> findByGuiaId(UUID guiaId);

    boolean existsByGuiaIdAndFechaSalidaIsNull(UUID guiaId);
}