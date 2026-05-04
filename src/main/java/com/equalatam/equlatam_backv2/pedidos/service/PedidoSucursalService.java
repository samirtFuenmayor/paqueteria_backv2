package com.equalatam.equlatam_backv2.pedidos.service;

import com.equalatam.equlatam_backv2.cliente.dto.response.ClienteResponse;
import com.equalatam.equlatam_backv2.cliente.entity.Cliente;
import com.equalatam.equlatam_backv2.cliente.repositories.ClienteRepository;
import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.exception.ResourceNotFoundException;
import com.equalatam.equlatam_backv2.pedidos.dto.request.PedidoRequest;
import com.equalatam.equlatam_backv2.pedidos.dto.response.PedidoResponse;
import com.equalatam.equlatam_backv2.pedidos.repository.PedidoRepository;
import com.equalatam.equlatam_backv2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoSucursalService {

    private final PedidoService     pedidoService;
    private final ClienteRepository clienteRepository;
    private final UserRepository    userRepository;
    private final PedidoRepository  pedidoRepository;

    // ─── Buscar cliente por cédula ────────────────────────────────────────────
    public ClienteResponse buscarClientePorCedula(String cedula) {
        Cliente cliente = clienteRepository.findByNumeroIdentificacion(cedula)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró cliente con cédula: " + cedula));
        return ClienteResponse.from(cliente);
    }

    // ─── Registrar pedido presencial ──────────────────────────────────────────
    @Transactional
    public PedidoResponse registrarPedidoPresencial(PedidoRequest req, String username) {

        // Obtener el agente/admin autenticado
        User agente = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado: " + username));

        // Validar que el agente tenga sucursal asignada
        if (agente.getSucursal() == null) {
            throw new IllegalArgumentException(
                    "El agente no tiene sucursal asignada. " +
                            "Contacta al administrador para asignarte una sucursal.");
        }

        // Usar el mismo flujo de creación del PedidoService
        PedidoResponse response = pedidoService.create(req, username);

        // Agregar los datos presenciales al pedido recién creado
        var pedido = pedidoRepository.findByNumeroPedido(response.numeroPedido())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        pedido.setRegistradoEnSucursal(true);
        pedido.setSucursalAtencion(agente.getSucursal());

        return PedidoResponse.from(pedidoRepository.save(pedido));
    }

    // ─── Pedidos de la sucursal del agente ───────────────────────────────────
    public List<PedidoResponse> pedidosDeSucursalAgente(String username) {
        User agente = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado: " + username));

        if (agente.getSucursal() == null) {
            throw new IllegalArgumentException("El agente no tiene sucursal asignada");
        }

        return pedidoRepository
                .findBySucursalAtencionId(agente.getSucursal().getId())
                .stream()
                .map(PedidoResponse::from)
                .collect(Collectors.toList());
    }
}