package com.equalatam.equlatam_backv2.sucursales.repository;

import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import com.equalatam.equlatam_backv2.sucursales.entity.TipoSucursal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SucursalRepository extends JpaRepository<Sucursal, UUID> {

    Optional<Sucursal> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByPrefijoCasillero(String prefijoCasillero);

    List<Sucursal> findByActivaTrue();

    List<Sucursal> findByTipo(TipoSucursal tipo);

    List<Sucursal> findByPaisAndActivaTrue(String pais);
}
