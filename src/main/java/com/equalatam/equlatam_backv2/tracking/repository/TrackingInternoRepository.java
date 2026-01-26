package com.equalatam.equlatam_backv2.tracking.repository;

import com.equalatam.equlatam_backv2.guias.Enums;
import com.equalatam.equlatam_backv2.tracking.entity.TrackingInterno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TrackingInternoRepository
        extends JpaRepository<TrackingInterno, UUID> {

    List<TrackingInterno> findByGuia_Id(UUID guiaId);
}


