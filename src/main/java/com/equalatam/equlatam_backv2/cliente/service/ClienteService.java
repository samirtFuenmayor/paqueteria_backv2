package com.equalatam.equlatam_backv2.cliente.service;

import com.equalatam.equlatam_backv2.cliente.dto.request.ClienteRegistroRequest;
import com.equalatam.equlatam_backv2.cliente.dto.request.ClienteRequest;
import com.equalatam.equlatam_backv2.cliente.dto.response.ClienteResponse;
import com.equalatam.equlatam_backv2.cliente.entity.Cliente;
import com.equalatam.equlatam_backv2.cliente.entity.EstadoCliente;
import com.equalatam.equlatam_backv2.cliente.entity.Parentesco;
import com.equalatam.equlatam_backv2.cliente.repositories.ClienteRepository;
import com.equalatam.equlatam_backv2.entity.Role;
import com.equalatam.equlatam_backv2.entity.User;
import com.equalatam.equlatam_backv2.exception.ResourceNotFoundException;
import com.equalatam.equlatam_backv2.repository.RoleRepository;
import com.equalatam.equlatam_backv2.repository.UserRepository;
import com.equalatam.equlatam_backv2.sucursales.entity.Sucursal;
import com.equalatam.equlatam_backv2.sucursales.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository    clienteRepository;
    private final SucursalRepository   sucursalRepository;
    private final UserRepository       userRepository;
    private final RoleRepository       roleRepository;
    private final PasswordEncoder      encoder;

    // ─────────────────────────────────────────────────────────────────────────
    // AUTO-REGISTRO: el cliente se registra solo desde la app / web
    // → crea su User con mustChangePassword = FALSE (puso su propia contraseña)
    // ─────────────────────────────────────────────────────────────────────────
    @Transactional
    public ClienteResponse registrar(ClienteRegistroRequest req) {

        validarDuplicados(req.numeroIdentificacion(), req.email());

        // 1. Crear User
        User user = crearUser(
                req.nombres(), req.apellidos(),
                req.numeroIdentificacion(),   // username = cédula/ruc/pasaporte
                req.email(), req.telefono(),
                req.password(),
                false   // mustChangePassword = false (se registró solo)
        );

        // 2. Crear Cliente
        Cliente c = new Cliente();
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
        c.setUser(user);

        // 3. Sucursal y casillero (opcional)
        if (req.sucursalId() != null) {
            Sucursal suc = getSucursalOrThrow(req.sucursalId());
            c.setSucursalAsignada(suc);
            c.setCasillero(generarCasillero(suc));
        }

        // 4. Titular / familia (opcional)
        if (req.titularId() != null) {
            Cliente titular = getClienteOrThrow(req.titularId());
            c.setTitular(titular);
            c.setParentesco(req.parentesco() != null ? req.parentesco() : Parentesco.OTRO);
        }
        return ClienteResponse.from(clienteRepository.save(c));
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

    // ─────────────────────────────────────────────────────────────────────────
    // CREAR POR ADMIN: el administrador crea el cliente
    // → mustChangePassword = true si viene password temporal, false si no
    // ─────────────────────────────────────────────────────────────────────────
    @Transactional
    public ClienteResponse create(ClienteRequest req) {

        validarDuplicados(req.numeroIdentificacion(), req.email());

        // Si el admin manda password → la marca como temporal (mustChangePassword = true)
        // Si no manda password → genera una por defecto temporal
        String rawPassword = (req.passwordTemporal() != null && !req.passwordTemporal().isBlank())
                ? req.passwordTemporal()
                : req.numeroIdentificacion(); // contraseña default = su número de identificación

        boolean mustChange = true; // siempre debe cambiar si la creó el admin

        User user = crearUser(
                req.nombres(), req.apellidos(),
                req.numeroIdentificacion(),
                req.email(), req.telefono(),
                rawPassword,
                mustChange
        );

        Cliente c = new Cliente();
        mapToEntity(req, c);
        c.setUser(user);

        // Titular si lo manda el admin
        if (req.titularId() != null) {
            Cliente titular = getClienteOrThrow(req.titularId());
            c.setTitular(titular);
            c.setParentesco(req.parentesco() != null ? req.parentesco() : Parentesco.OTRO);
        }

        if (c.getSucursalAsignada() != null) {
            c.setCasillero(generarCasillero(c.getSucursalAsignada()));
        }

        return ClienteResponse.from(clienteRepository.save(c));
    }


    @Transactional
    public ClienteResponse gestionarTitular(UUID clienteId, UUID titularId, Parentesco parentesco) {
        Cliente cliente = getClienteOrThrow(clienteId);

        if (titularId == null) {
            cliente.setTitular(null);
            cliente.setParentesco(null);
            return ClienteResponse.from(clienteRepository.save(cliente));
        }

        if (clienteId.equals(titularId))
            throw new IllegalArgumentException("Un cliente no puede ser titular de sí mismo");

        Cliente titular = getClienteOrThrow(titularId);

        if (titular.getTitular() != null && titular.getTitular().getId().equals(clienteId))
            throw new IllegalArgumentException(
                    "No se puede crear una referencia circular entre titular y afiliado");

        cliente.setTitular(titular);
        cliente.setParentesco(parentesco != null ? parentesco : Parentesco.OTRO);

        return ClienteResponse.from(clienteRepository.save(cliente));
    }

    // ─── Listar activos ───────────────────────────────────────────────────────
    public List<ClienteResponse> findAll() {
        return clienteRepository.findByEstado(EstadoCliente.ACTIVO)
                .stream().map(ClienteResponse::from).collect(Collectors.toList());
    }

    public List<ClienteResponse> findAllIncluyendoInactivos() {
        return clienteRepository.findAll()
                .stream().map(ClienteResponse::from).collect(Collectors.toList());
    }

    public ClienteResponse findById(UUID id) {
        return ClienteResponse.from(getClienteOrThrow(id));
    }

    public ClienteResponse findByIdentificacion(String numeroIdentificacion) {
        return ClienteResponse.from(
                clienteRepository.findByNumeroIdentificacion(numeroIdentificacion)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Cliente no encontrado: " + numeroIdentificacion)));
    }

    public ClienteResponse findByCasillero(String casillero) {
        return ClienteResponse.from(
                clienteRepository.findByCasillero(casillero)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Cliente no encontrado con casillero: " + casillero)));
    }

    public List<ClienteResponse> findBySucursal(UUID sucursalId) {
        return clienteRepository.findBySucursalAsignadaId(sucursalId)
                .stream().map(ClienteResponse::from).collect(Collectors.toList());
    }

    public List<ClienteResponse> buscar(String query) {
        return clienteRepository.buscar(query)
                .stream().map(ClienteResponse::from).collect(Collectors.toList());
    }

    // ─── Actualizar ───────────────────────────────────────────────────────────
    @Transactional
    public ClienteResponse update(UUID id, ClienteRequest req) {
        Cliente c = getClienteOrThrow(id);

        if (!c.getEmail().equals(req.email()) && clienteRepository.existsByEmail(req.email()))
            throw new IllegalArgumentException("Ya existe un cliente con el email: " + req.email());

        if (!c.getNumeroIdentificacion().equals(req.numeroIdentificacion()) &&
                clienteRepository.existsByNumeroIdentificacion(req.numeroIdentificacion()))
            throw new IllegalArgumentException("Ya existe un cliente con la identificación: " + req.numeroIdentificacion());

        String casilleroAnterior = c.getCasillero();
        mapToEntity(req, c);

        if (c.getSucursalAsignada() != null && casilleroAnterior == null)
            c.setCasillero(generarCasillero(c.getSucursalAsignada()));

        // Actualizar titular si viene en el request
        if (req.titularId() != null) {
            Cliente titular = getClienteOrThrow(req.titularId());
            c.setTitular(titular);
            c.setParentesco(req.parentesco() != null ? req.parentesco() : Parentesco.OTRO);
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

    // ─── Asignar sucursal ─────────────────────────────────────────────────────
    @Transactional
    public ClienteResponse asignarSucursal(UUID clienteId, UUID sucursalId) {
        Cliente c = getClienteOrThrow(clienteId);
        Sucursal sucursal = getSucursalOrThrow(sucursalId);
        c.setSucursalAsignada(sucursal);
        if (c.getCasillero() == null)
            c.setCasillero(generarCasillero(sucursal));
        return ClienteResponse.from(clienteRepository.save(c));
    }

    // ─── Listar afiliados de un titular ──────────────────────────────────────
    public List<ClienteResponse> findAfiliados(UUID titularId) {
        return clienteRepository.findByTitularId(titularId)
                .stream().map(ClienteResponse::from).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS PRIVADOS
    // ─────────────────────────────────────────────────────────────────────────

    private User crearUser(String nombre, String apellido, String username,
                           String correo, String telefono,
                           String rawPassword, boolean mustChangePassword) {

        // Verificar que el username no exista ya en users
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con username: " + username);
        }

        Role rolCliente = roleRepository.findByName("CLIENTE")
                .orElseThrow(() -> new IllegalStateException(
                        "El rol CLIENTE no existe. Créalo primero en /api/roles"));

        User user = new User();
        user.setNombre(nombre);
        user.setApellido(apellido);
        user.setUsername(username);
        user.setCorreo(correo);
        user.setTelefono(telefono);
        user.setPassword(encoder.encode(rawPassword));
        user.setMustChangePassword(mustChangePassword);
        user.setRoles(Set.of(rolCliente));

        return userRepository.save(user);
    }

    private void validarDuplicados(String identificacion, String email) {
        if (clienteRepository.existsByNumeroIdentificacion(identificacion))
            throw new IllegalArgumentException(
                    "Ya existe un cliente con la identificación: " + identificacion);
        if (clienteRepository.existsByEmail(email))
            throw new IllegalArgumentException(
                    "Ya existe un cliente con el email: " + email);
    }

    private String generarCasillero(Sucursal sucursal) {
        String prefijo = sucursal.getPrefijoCasillero();
        long count = clienteRepository.findBySucursalAsignadaId(sucursal.getId())
                .stream().filter(cl -> cl.getCasillero() != null).count();
        long siguiente = count + 1;
        String casillero = String.format("%s-%05d", prefijo, siguiente);
        while (clienteRepository.existsByCasillero(casillero)) {
            siguiente++;
            casillero = String.format("%s-%05d", prefijo, siguiente);
        }
        return casillero;
    }

    private Cliente getClienteOrThrow(UUID id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado: " + id));
    }

    private Sucursal getSucursalOrThrow(UUID id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sucursal no encontrada: " + id));
    }
}