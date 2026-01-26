package com.equalatam.equlatam_backv2.rutas.repository;

import com.equalatam.equlatam_backv2.rutas.Enums;
import com.equalatam.equlatam_backv2.rutas.entity.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RutaRepository extends JpaRepository<Ruta, UUID> {

    List<Ruta> findByTipo(Enums.TipoRuta tipo);

    List<Ruta> findByEstado(Enums.EstadoRuta estado);
}