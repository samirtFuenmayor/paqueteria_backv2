package com.equalatam.equlatam_backv2.pedidos.entity;


import com.equalatam.equlatam_backv2.cliente.entity.Cliente;
import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
@Getter @Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue
    private UUID id;

    // ─── Número de pedido único ───────────────────────────────────────────────
    // Formato: PED-2024-00001
    @Column(unique = true, nullable = false)
    private String numeroPedido;

    // ─── Tipo ─────────────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPedido tipo;

    // ─── Cliente propietario ──────────────────────────────────────────────────
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // ─── Tracking externo (Amazon, FedEx, UPS, etc.) ─────────────────────────
    private String trackingExterno;     // Número de tracking del proveedor
    private String proveedor;           // Amazon, eBay, FedEx, etc.
    private String urlTracking;         // Link al tracking del proveedor

    // ─── Descripción del contenido ────────────────────────────────────────────
    @Column(nullable = false)
    private String descripcion;

    private Double peso;                // En libras
    private Double largo;               // Dimensiones en cm
    private Double ancho;
    private Double alto;
    private Double valorDeclarado;      // En USD
    private Integer cantidadItems;

    // ─── Sucursales involucradas ──────────────────────────────────────────────
    // Donde llega el paquete primero (EEUU o Canadá)
    @ManyToOne
    @JoinColumn(name = "sucursal_origen_id")
    private Sucursal sucursalOrigen;

    // Sucursal en Ecuador donde el cliente retirará
    @ManyToOne
    @JoinColumn(name = "sucursal_destino_id")
    private Sucursal sucursalDestino;

    // ─── Estado actual ────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado = EstadoPedido.REGISTRADO;

    // ─── Empleado que registró el pedido ─────────────────────────────────────
    @ManyToOne
    @JoinColumn(name = "registrado_por_id")
    private User registradoPor;

    // ─── Fechas importantes ───────────────────────────────────────────────────
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    private LocalDateTime fechaRecepcionSede;       // Cuando llegó a sede exterior
    private LocalDateTime fechaSalidaExterior;      // Cuando salió hacia Ecuador
    private LocalDateTime fechaLlegadaEcuador;      // Cuando llegó a Ecuador
    private LocalDateTime fechaDisponible;          // Cuando está listo para retiro
    private LocalDateTime fechaEntrega;             // Cuando fue entregado

    // ─── Observaciones y notas internas ──────────────────────────────────────
    private String observaciones;
    private String notasInternas;       // Solo visibles para empleados

    // ─── Fotos / evidencias ───────────────────────────────────────────────────
    private String fotoUrl;             // URL de foto del paquete al recibir

    private LocalDateTime actualizadoEn;

    @PreUpdate
    public void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }

    // ─── Categoría del pedido ─────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    private CategoriaPedido categoria;

    // ─── Pedido por titular ───────────────────────────────────────────────────────
    private Boolean esPorTitular = false;

    @ManyToOne
    @JoinColumn(name = "titular_id")
    private com.equalatam.equlatam_backv2.cliente.entity.Cliente titular;

    // ─── Items del pedido ─────────────────────────────────────────────────────────
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<PedidoItem> items = new java.util.ArrayList<>();

    // ─── Peso total calculado ─────────────────────────────────────────────────────
    private Double pesoTotal = 0.0;

    // ─── Tarifa aplicada ──────────────────────────────────────────────────────────
    private String tipoTarifa = "INDIVIDUAL"; // INDIVIDUAL, FAMILIAR, AMIGO

    // ─── Pago ─────────────────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormaPago formaPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estadoPago = EstadoPago.PENDIENTE_COMPROBANTE;

    // Datos bancarios (solo para TRANSFERENCIA)
    private String bancoOrigen;
    private String numeroReferencia;

    // Comprobante en base64 (transferencia o foto del recibo efectivo)
    @Column(columnDefinition = "TEXT")
    private String comprobanteBase64;

    private LocalDateTime fechaSubidaComprobante;
    private LocalDateTime fechaVerificacionPago;

    // Quien verificó el pago
    @ManyToOne
    @JoinColumn(name = "verificado_por_id")
    private com.equalatam.equlatam_backv2.entity.User verificadoPor;

    private String motivoRechazo;

    // ─── Facturación ──────────────────────────────────────────────────────────────
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "razonSocial",          column = @Column(name = "fact_razon_social")),
            @AttributeOverride(name = "rucCedula",            column = @Column(name = "fact_ruc_cedula")),
            @AttributeOverride(name = "direccionFacturacion", column = @Column(name = "fact_direccion")),
            @AttributeOverride(name = "emailFacturacion",     column = @Column(name = "fact_email")),
            @AttributeOverride(name = "telefonoFacturacion",  column = @Column(name = "fact_telefono")),
            @AttributeOverride(name = "usarDatosCliente",     column = @Column(name = "fact_usar_datos_cliente"))
    })
    private DatosFacturacion datosFacturacion;

    // ─── Sucursal donde se atendió presencialmente ────────────────────────────────
// Solo aplica cuando el pedido fue registrado por un agente/admin en sucursal
    @ManyToOne
    @JoinColumn(name = "sucursal_atencion_id")
    private Sucursal sucursalAtencion;

    // true = pedido registrado presencialmente por agente/admin
    @Column(nullable = false)
    private boolean registradoEnSucursal = false;
}