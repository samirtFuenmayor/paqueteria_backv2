package com.equalatam.equlatam_backv2.guias.entity;

import com.equalatam.equlatam_backv2.cliente.entity.Cliente;
import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.pedidos.entity.Pedido;
import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "guias")
@Getter @Setter
@NoArgsConstructor
public class Guia {

    @Id
    @GeneratedValue
    private UUID id;

    // ─── Número único de guía ─────────────────────────────────────────────────
    // Formato: GUI-2026-00001
    @Column(unique = true, nullable = false)
    private String numeroGuia;

    // ─── Pedido asociado ──────────────────────────────────────────────────────
    @OneToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    // ─── Remitente ────────────────────────────────────────────────────────────
    @Column(nullable = false)
    private String remitenteNombre;
    private String remitenteDireccion;
    private String remitenteTelefono;
    private String remitenteEmail;
    private String remitentePais;

    // ─── Destinatario (cliente) ───────────────────────────────────────────────
    @ManyToOne
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Cliente destinatario;

    // ─── Sucursales ───────────────────────────────────────────────────────────
    @ManyToOne
    @JoinColumn(name = "sucursal_origen_id")
    private Sucursal sucursalOrigen;

    @ManyToOne
    @JoinColumn(name = "sucursal_destino_id")
    private Sucursal sucursalDestino;

    // ─── Contenido declarado ──────────────────────────────────────────────────
    @Column(nullable = false)
    private String descripcionContenido;

    private Double pesoDeclarado;       // En libras
    private Double pesoVolumetrico;     // Calculado: L x A x H / 166
    private Double pesoCobrable;        // El mayor entre real y volumétrico
    private Double valorDeclarado;      // En USD
    private Integer cantidadPiezas;

    private Double largo;
    private Double ancho;
    private Double alto;

    // ─── Información de transporte ────────────────────────────────────────────
    private String numeroDespacho;      // Referencia al despacho
    private String aerolinea;
    private String numeroVuelo;
    private String guiaAerea;           // AWB del despacho

    // ─── Tarifas aplicadas ────────────────────────────────────────────────────
    private Double tarifaPorLibra;
    private Double costoFlete;          // pesosCobrable * tarifaPorLibra
    private Double costoManejo;         // Cargo fijo por manejo
    private Double costoSeguro;         // % del valor declarado
    private Double costoTotal;          // Total a cobrar al cliente

    // ─── Estado ───────────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoGuia estado = EstadoGuia.GENERADA;

    // ─── Auditoría ────────────────────────────────────────────────────────────
    @ManyToOne
    @JoinColumn(name = "generada_por_id")
    private User generadaPor;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaGeneracion = LocalDateTime.now();

    private LocalDateTime fechaEntrega;
    private String observaciones;
}
