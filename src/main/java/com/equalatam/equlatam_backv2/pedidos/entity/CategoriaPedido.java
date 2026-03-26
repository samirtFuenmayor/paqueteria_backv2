package com.equalatam.equlatam_backv2.pedidos.entity;

public enum CategoriaPedido {
    FOUR_X_TWO,     // 4x2: hasta 2 kg / 4.4 lb — libre de impuestos
    FOUR_X_FOUR,    // 4x4: hasta 4 kg / 8.8 lb — hasta $400, paga $20 fijo
    CARGA_GENERAL,  // Sin límite de peso — arancel + IVA
    DOCUMENTO       // Solo documentos — libre
}