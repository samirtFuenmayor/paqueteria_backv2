package com.equalatam.equlatam_backv2.sucursales.repository;

import com.equalatam.equlatam_backv2.sucursales.Enums;
import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SucursalRepository extends JpaRepository<Sucursal, UUID> {

    List<Sucursal> findByEstado(Enums.EstadoSucursal estado);
}