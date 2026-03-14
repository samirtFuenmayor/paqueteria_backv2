package com.equalatam.equlatam_backv2.financiero.entity;

import com.equalatam.equlatam_backv2.financiero.enums.CategoriaPaquete;
import com.equalatam.equlatam_backv2.pedidos.entity.TipoPedido;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tarifas_financiero")
@Getter @Setter
@NoArgsConstructor
public class Tarifa {

    @Id
    @GeneratedValue
    private UUID id;

    // ─── Identificación ───────────────────────────────────────────────────────
    @Column(nullable = false)
    private String nombre;          // Ej: "Sobre Express Importación"

    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaPaquete categoria;

    // IMPORTACION, EXPORTACION — usamos el enum que ya tienes en pedidos
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPedido tipoPedido;   // IMPORTACION / EXPORTACION

    // ─── Estructura de precio ─────────────────────────────────────────────────
    // Precio fijo base (se cobra siempre, independiente del peso)
    @Column(nullable = false)
    private Double precioBase = 0.0;

    // Precio adicional por libra (sobre el peso mínimo si aplica)
    private Double precioPorLibra = 0.0;

    // Peso mínimo facturable (si el paquete pesa menos, se cobra este mínimo)
    private Double pesoMinimo = 0.0;

    // Precio por cm³ para cálculo de peso volumétrico
    private Double precioPorCm3 = 0.0;

    // Factor divisor para convertir volumen a peso equivalente
    // Estándar aéreo internacional: 139 (pulgadas) o 5000 (cm)
    private Double factorDivisorVolumetrico = 139.0;

    // Porcentaje sobre valor declarado (ej: seguro de carga = 1.5%)
    private Double porcentajeSobreValorDeclarado = 0.0;

    // ─── Rango de peso aplicable ──────────────────────────────────────────────
    private Double pesoDesde;   // null = sin límite inferior
    private Double pesoHasta;   // null = sin límite superior

    // ─── IVA ──────────────────────────────────────────────────────────────────
    // Ecuador: 15% (2024-2025). Se guarda aquí para flexibilidad futura.
    @Column(nullable = false)
    private Double porcentajeIva = 15.0;

    // ─── Vigencia ─────────────────────────────────────────────────────────────
    private LocalDate vigenciaDesde;
    private LocalDate vigenciaHasta;

    @Column(nullable = false)
    private Boolean activo = true;

    // ─── Auditoría ────────────────────────────────────────────────────────────
    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    private LocalDateTime actualizadoEn;

    @PreUpdate
    public void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }

    // ─── Método de cálculo ────────────────────────────────────────────────────
    /**
     * Calcula el subtotal (sin IVA) para un pedido dado.
     *
     * @param pesoReal       peso en libras
     * @param largo          en cm
     * @param ancho          en cm
     * @param alto           en cm
     * @param valorDeclarado en USD
     * @return subtotal antes de IVA
     */
    public Double calcularSubtotal(Double pesoReal, Double largo, Double ancho,
                                   Double alto, Double valorDeclarado) {
        double total = precioBase;

        // Peso volumétrico = (largo × ancho × alto) / factor
        double pesoVolumetrico = 0.0;
        if (largo != null && ancho != null && alto != null && factorDivisorVolumetrico > 0) {
            pesoVolumetrico = (largo * ancho * alto) / factorDivisorVolumetrico;
        }

        // Peso facturable = mayor entre peso real y volumétrico
        double pesoFacturable = Math.max(
                pesoReal != null ? pesoReal : 0.0,
                pesoVolumetrico
        );

        // Aplicar peso mínimo
        if (pesoMinimo != null && pesoMinimo > 0) {
            pesoFacturable = Math.max(pesoFacturable, pesoMinimo);
        }

        // Cobro por libra
        if (precioPorLibra != null && precioPorLibra > 0) {
            total += pesoFacturable * precioPorLibra;
        }

        // Cobro por volumen (cm³)
        if (precioPorCm3 != null && precioPorCm3 > 0 && largo != null) {
            total += (largo * ancho * alto) * precioPorCm3;
        }

        // Cobro sobre valor declarado
        if (porcentajeSobreValorDeclarado != null && porcentajeSobreValorDeclarado > 0
                && valorDeclarado != null && valorDeclarado > 0) {
            total += valorDeclarado * (porcentajeSobreValorDeclarado / 100.0);
        }

        return Math.round(total * 100.0) / 100.0;
    }
}