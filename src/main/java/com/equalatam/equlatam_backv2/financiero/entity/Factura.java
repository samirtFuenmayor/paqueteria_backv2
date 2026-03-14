package com.equalatam.equlatam_backv2.financiero.entity;

import com.equalatam.equlatam_backv2.cliente.entity.Cliente;
import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.financiero.enums.*;
import com.equalatam.equlatam_backv2.pedidos.entity.Pedido;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "facturas_financiero")
@Getter @Setter
@NoArgsConstructor
public class Factura {

    @Id
    @GeneratedValue
    private UUID id;

    // ─── Numeración SRI: formato 001-001-000000001 ────────────────────────────
    @Column(unique = true)
    private String numeroFactura;       // 001-001-000000001 (número completo)

    @Column(nullable = false)
    private String establecimiento;     // 001 (punto de emisión físico)

    @Column(nullable = false)
    private String puntoEmision;        // 001 (caja/punto de venta)

    @Column(nullable = true)
    private Long secuencial;            // 1, 2, 3... autoincremental por establecimiento+punto

    // Clave de acceso SRI (49 dígitos) — para integración futura con SRI
    private String claveAccesoSri;

    // ─── Tipo de documento ────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDocumento tipoDocumento = TipoDocumento.FACTURA;

    // ─── Relaciones ───────────────────────────────────────────────────────────
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "cotizacion_id")
    private Cotizacion cotizacion;

    // ─── Detalles (líneas de factura) ─────────────────────────────────────────
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<FacturaDetalle> detalles = new ArrayList<>();

    // ─── Montos ───────────────────────────────────────────────────────────────
    // Subtotal gravado con tarifa 0% (servicios exentos)
    @Column(nullable = false)
    private Double subtotal0 = 0.0;

    // Subtotal gravado con IVA 15%
    @Column(nullable = false)
    private Double subtotal15 = 0.0;

    // Descuento total
    @Column(nullable = false)
    private Double descuento = 0.0;

    // Monto del IVA 15%
    @Column(nullable = false)
    private Double iva = 0.0;

    // Total a pagar
    @Column(nullable = false)
    private Double total = 0.0;

    // ─── Pago ─────────────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormaPago formaPago = FormaPago.EFECTIVO;

    // ─── Estado ───────────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoFactura estado = EstadoFactura.BORRADOR;

    // ─── Fechas ───────────────────────────────────────────────────────────────
    @Column(nullable = false)
    private LocalDate fechaEmision;

    private LocalDate fechaVencimiento;     // Para facturas a crédito

    // ─── Datos del emisor (tu empresa) — se guardan al emitir ─────────────────
    private String emisorRuc;
    private String emisorRazonSocial;
    private String emisorDireccion;

    // ─── Observaciones ────────────────────────────────────────────────────────
    @Column(columnDefinition = "TEXT")
    private String observaciones;

    // ─── Nota de crédito/débito relacionada ───────────────────────────────────
    @OneToMany(mappedBy = "facturaOrigen", cascade = CascadeType.ALL)
    private List<NotaAjuste> notasAjuste = new ArrayList<>();

    // ─── Pagos registrados ────────────────────────────────────────────────────
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    private List<Pago> pagos = new ArrayList<>();

    // ─── Auditoría ────────────────────────────────────────────────────────────
    @ManyToOne
    @JoinColumn(name = "emitido_por_id")
    private User emitidoPor;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    private LocalDateTime actualizadoEn;

    @PreUpdate
    public void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }

    // ─── Helper: recalcular totales desde detalles ────────────────────────────
    public void recalcularTotales() {
        this.subtotal0  = 0.0;
        this.subtotal15 = 0.0;
        this.descuento  = 0.0;

        for (FacturaDetalle d : detalles) {
            double subLinea = d.getPrecioUnitario() * d.getCantidad();
            double descLinea = d.getDescuento() != null ? d.getDescuento() : 0.0;
            double neto = subLinea - descLinea;

            this.descuento += descLinea;

            if (d.isGravaIva()) {
                this.subtotal15 += neto;
            } else {
                this.subtotal0  += neto;
            }
        }

        this.iva   = Math.round(this.subtotal15 * 0.15 * 100.0) / 100.0;
        this.total = Math.round((this.subtotal0 + this.subtotal15 + this.iva) * 100.0) / 100.0;
    }
}