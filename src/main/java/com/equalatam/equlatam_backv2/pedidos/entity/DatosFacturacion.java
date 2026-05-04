package com.equalatam.equlatam_backv2.pedidos.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter @Setter
@NoArgsConstructor
public class DatosFacturacion {

    // Puede ser del cliente o de un tercero
    private String razonSocial;         // Nombre completo o empresa
    private String rucCedula;           // RUC o cédula
    private String direccionFacturacion;
    private String emailFacturacion;
    private String telefonoFacturacion;

    // true = usar datos del cliente, false = datos de tercero
    private Boolean usarDatosCliente = true;
}