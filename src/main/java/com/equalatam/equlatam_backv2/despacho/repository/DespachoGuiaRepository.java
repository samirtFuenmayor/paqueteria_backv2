package com.equalatam.equlatam_backv2.despacho.repository;

import com.equalatam.equlatam_backv2.despacho.entity.DespachoGuia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DespachoGuiaRepository extends JpaRepository<DespachoGuia, UUID> {

    List<DespachoGuia> findByDespachoId(UUID despachoId);

    boolean existsByGuiaId(UUID guiaId);
}