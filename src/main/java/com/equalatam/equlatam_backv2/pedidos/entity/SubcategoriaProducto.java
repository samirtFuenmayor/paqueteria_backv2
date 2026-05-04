package com.equalatam.equlatam_backv2.pedidos.entity;

public enum SubcategoriaProducto {

    // ─── ELECTRONICO ──────────────────────────────────────────────────────────
    LAPTOP(TipoProducto.ELECTRONICO),
    CELULAR(TipoProducto.ELECTRONICO),
    TABLET(TipoProducto.ELECTRONICO),
    SMARTWATCH(TipoProducto.ELECTRONICO),
    AURICULARES(TipoProducto.ELECTRONICO),
    CAMARA(TipoProducto.ELECTRONICO),
    CONSOLA_VIDEOJUEGOS(TipoProducto.ELECTRONICO),
    COMPONENTE_PC(TipoProducto.ELECTRONICO),
    OTRO_ELECTRONICO(TipoProducto.ELECTRONICO),

    // ─── ROPA ─────────────────────────────────────────────────────────────────
    ROPA_HOMBRE(TipoProducto.ROPA),
    ROPA_MUJER(TipoProducto.ROPA),
    ROPA_NINO(TipoProducto.ROPA),
    CALZADO(TipoProducto.ROPA),
    ACCESORIO_MODA(TipoProducto.ROPA),
    BOLSO_CARTERA(TipoProducto.ROPA),
    OTRO_TEXTIL(TipoProducto.ROPA),

    // ─── COSMETICO ────────────────────────────────────────────────────────────
    PERFUME(TipoProducto.COSMETICO),
    CREMA_LOCION(TipoProducto.COSMETICO),
    MAQUILLAJE(TipoProducto.COSMETICO),
    SUPLEMENTO_BELLEZA(TipoProducto.COSMETICO),
    OTRO_COSMETICO(TipoProducto.COSMETICO),

    // ─── ALIMENTO ─────────────────────────────────────────────────────────────
    SUPLEMENTO_DEPORTIVO(TipoProducto.ALIMENTO),
    SNACK_GOLOSINA(TipoProducto.ALIMENTO),
    VITAMINA_MEDICAMENTO_OTC(TipoProducto.ALIMENTO),
    OTRO_ALIMENTO(TipoProducto.ALIMENTO),

    // ─── HERRAMIENTA ──────────────────────────────────────────────────────────
    HERRAMIENTA_ELECTRICA(TipoProducto.HERRAMIENTA),
    HERRAMIENTA_MANUAL(TipoProducto.HERRAMIENTA),
    REPUESTO_AUTOMOTRIZ(TipoProducto.HERRAMIENTA),
    REPUESTO_INDUSTRIAL(TipoProducto.HERRAMIENTA),
    OTRO_REPUESTO(TipoProducto.HERRAMIENTA),

    // ─── JUGUETE ──────────────────────────────────────────────────────────────
    JUGUETE_INFANTIL(TipoProducto.JUGUETE),
    ARTICULO_BEBE(TipoProducto.JUGUETE),
    JUEGO_MESA(TipoProducto.JUGUETE),
    FIGURA_COLECCIONABLE(TipoProducto.JUGUETE),
    OTRO_JUGUETE(TipoProducto.JUGUETE),

    // ─── LIBRO ────────────────────────────────────────────────────────────────
    LIBRO_TECNICO(TipoProducto.LIBRO),
    LIBRO_TEXTO(TipoProducto.LIBRO),
    NOVELA_LITERATURA(TipoProducto.LIBRO),
    REVISTA(TipoProducto.LIBRO),
    OTRO_LIBRO(TipoProducto.LIBRO),

    // ─── DOCUMENTO ────────────────────────────────────────────────────────────
    DOCUMENTO_LEGAL(TipoProducto.DOCUMENTO),
    DOCUMENTO_ACADEMICO(TipoProducto.DOCUMENTO),
    DOCUMENTO_COMERCIAL(TipoProducto.DOCUMENTO),
    OTRO_DOCUMENTO(TipoProducto.DOCUMENTO),

    // ─── OTRO ─────────────────────────────────────────────────────────────────
    ARTICULO_HOGAR(TipoProducto.OTRO),
    DEPORTE_FITNESS(TipoProducto.OTRO),
    MASCOTA_VETERINARIA(TipoProducto.OTRO),
    SIN_CLASIFICAR(TipoProducto.OTRO);

    private final TipoProducto tipo;

    SubcategoriaProducto(TipoProducto tipo) {
        this.tipo = tipo;
    }

    public TipoProducto getTipo() {
        return tipo;
    }

    // Verifica que esta subcategoría pertenezca al tipo dado
    public boolean perteneceA(TipoProducto tipoProducto) {
        return this.tipo == tipoProducto;
    }
}