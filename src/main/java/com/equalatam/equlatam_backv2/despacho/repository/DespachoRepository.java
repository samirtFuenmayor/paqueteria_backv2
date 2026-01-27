package com.equalatam.equlatam_backv2.despacho.repository;

import com.equalatam.equlatam_backv2.despacho.entity.Despacho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DespachoRepository extends JpaRepository<Despacho, UUID> {

    List<Despacho> findBySucursalId(UUID sucursalId);
}