package com.equalatam.equlatam_backv2.cliente.service;


import com.equalatam.equlatam_backv2.cliente.dto.request.ClienteRequest;
import com.equalatam.equlatam_backv2.cliente.dto.response.ClienteResponse;
import com.equalatam.equlatam_backv2.cliente.entity.Cliente;
import com.equalatam.equlatam_backv2.cliente.entity.EstadoCliente;
import com.equalatam.equlatam_backv2.cliente.repositories.ClienteRepository;
import com.equalatam.equlatam_backv2.exception.ResourceNotFoundException;
import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import com.equalatam.equlatam_backv2.sucursales.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final SucursalRepository sucursalRepository;

    // ─── Crear cliente con casillero automático ────────────────────────────────
    @Transactional
    public ClienteResponse create(ClienteRequest req) {

        // Validar duplicados
        if (clienteRepository.existsByNumeroIdentificacion(req.numeroIdentificacion())) {
            throw new IllegalArgumentException(
                    "Ya existe un cliente con la identificación: " + req.numeroIdentificacion());
        }
        if (clienteRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException(
                    "Ya existe un cliente con el email: " + req.email());
        }

        Cliente c = new Cliente();
        mapToEntity(req, c);

        // Generar casillero automático si tiene sucursal asignada
        if (c.getSucursalAsignada() != null) {
            String casillero = generarCasillero(c.getSucursalAsignada());
            c.setCasillero(casillero);
        }

        return ClienteResponse.from(clienteRepository.save(c));
    }

    // ─── Listar todos los activos ─────────────────────────────────────────────
    public List<ClienteResponse> findAll() {
        return clienteRepository.findByEstado(EstadoCliente.ACTIVO)
                .stream().map(ClienteResponse::from).collect(Collectors.toList());
    }

    // ─── Listar todos (incluyendo inactivos) ──────────────────────────────────
    public List<ClienteResponse> findAllIncluyendoInactivos() {
        return clienteRepository.findAll()
                .stream().map(ClienteResponse::from).collect(Collectors.toList());
    }

    // ─── Buscar por ID ────────────────────────────────────────────────────────
    public ClienteResponse findById(UUID id) {
        return ClienteResponse.from(getClienteOrThrow(id));
    }

    // ─── Buscar por identificación ────────────────────────────────────────────
    public ClienteResponse findByIdentificacion(String numeroIdentificacion) {
        return ClienteResponse.from(
                clienteRepository.findByNumeroIdentificacion(numeroIdentificacion)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Cliente no encontrado con identificación: " + numeroIdentificacion))
        );
    }

    // ─── Buscar por casillero ─────────────────────────────────────────────────
    public ClienteResponse findByCasillero(String casillero) {
        return ClienteResponse.from(
                clienteRepository.findByCasillero(casillero)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Cliente no encontrado con casillero: " + casillero))
        );
    }

    // ─── Buscar clientes por sucursal ─────────────────────────────────────────
    public List<ClienteResponse> findBySucursal(UUID sucursalId) {
        return clienteRepository.findBySucursalAsignadaId(sucursalId)
                .stream().map(ClienteResponse::from).collect(Collectors.toList());
    }

    // ─── Buscador general (nombre, apellido, identificación, casillero) ───────
    public List<ClienteResponse> buscar(String query) {
        return clienteRepository.buscar(query)
                .stream().map(ClienteResponse::from).collect(Collectors.toList());
    }

    // ─── Actualizar ───────────────────────────────────────────────────────────
    @Transactional
    public ClienteResponse update(UUID id, ClienteRequest req) {
        Cliente c = getClienteOrThrow(id);

        // Validar email único solo si cambió
        if (!c.getEmail().equals(req.email()) && clienteRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Ya existe un cliente con el email: " + req.email());
        }

        // Validar identificación única solo si cambió
        if (!c.getNumeroIdentificacion().equals(req.numeroIdentificacion()) &&
                clienteRepository.existsByNumeroIdentificacion(req.numeroIdentificacion())) {
            throw new IllegalArgumentException(
                    "Ya existe un cliente con la identificación: " + req.numeroIdentificacion());
        }

        String casilleroAnterior = c.getCasillero();
        mapToEntity(req, c);

        // Regenerar casillero si cambió la sucursal y no tenía casillero
        if (c.getSucursalAsignada() != null && casilleroAnterior == null) {
            c.setCasillero(generarCasillero(c.getSucursalAsignada()));
        }

        return ClienteResponse.from(clienteRepository.save(c));
    }

    // ─── Cambiar estado ───────────────────────────────────────────────────────
    @Transactional
    public ClienteResponse cambiarEstado(UUID id, EstadoCliente nuevoEstado) {
        Cliente c = getClienteOrThrow(id);
        c.setEstado(nuevoEstado);
        return ClienteResponse.from(clienteRepository.save(c));
    }

    // ─── Asignar sucursal y generar casillero ─────────────────────────────────
    @Transactional
    public ClienteResponse asignarSucursal(UUID clienteId, UUID sucursalId) {
        Cliente c = getClienteOrThrow(clienteId);
        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sucursal no encontrada: " + sucursalId));

        c.setSucursalAsignada(sucursal);

        // Generar casillero si no tiene
        if (c.getCasillero() == null) {
            c.setCasillero(generarCasillero(sucursal));
        }

        return ClienteResponse.from(clienteRepository.save(c));
    }

    // ─── Generador automático de casillero ────────────────────────────────────
    // Formato: {PREFIJO}-{numero de 5 dígitos} → MIA-00123
    private String generarCasillero(Sucursal sucursal) {
        String prefijo = sucursal.getPrefijoCasillero();

        // Contar cuántos clientes ya tienen casillero de esta sucursal
        long count = clienteRepository.findBySucursalAsignadaId(sucursal.getId())
                .stream()
                .filter(cl -> cl.getCasillero() != null)
                .count();

        // Siguiente número
        long siguiente = count + 1;
        String casillero = String.format("%s-%05d", prefijo, siguiente);

        // Verificar que no exista (por si acaso)
        while (clienteRepository.existsByCasillero(casillero)) {
            siguiente++;
            casillero = String.format("%s-%05d", prefijo, siguiente);
        }

        return casillero;
    }

    // ─── Helper ───────────────────────────────────────────────────────────────
    private Cliente getClienteOrThrow(UUID id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con id: " + id));
    }

    private void mapToEntity(ClienteRequest req, Cliente c) {
        c.setTipoIdentificacion(req.tipoIdentificacion());
        c.setNumeroIdentificacion(req.numeroIdentificacion());
        c.setNombres(req.nombres());
        c.setApellidos(req.apellidos());
        c.setEmail(req.email());
        c.setTelefono(req.telefono());
        c.setFechaNacimiento(req.fechaNacimiento());
        c.setPais(req.pais());
        c.setProvincia(req.provincia());
        c.setCiudad(req.ciudad());
        c.setDireccion(req.direccion());
        c.setObservaciones(req.observaciones());

        if (req.sucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(req.sucursalId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Sucursal no encontrada: " + req.sucursalId()));
            c.setSucursalAsignada(sucursal);
        }
    }
}

