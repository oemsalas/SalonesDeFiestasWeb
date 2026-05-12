package com.salon.fiestas.service;

import com.salon.fiestas.exception.RecursoNoEncontradoException;
import com.salon.fiestas.exception.ReglaNegocioException;
import com.salon.fiestas.model.dto.ClienteDTO;
import com.salon.fiestas.model.entity.Cliente;
import com.salon.fiestas.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<ClienteDTO.Response> listar() {
        return clienteRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ClienteDTO.Response obtener(Long id) {
        return toResponse(buscarOFallar(id));
    }

    @Transactional
    public ClienteDTO.Response crear(ClienteDTO.Request request) {
        if (clienteRepository.existsByEmail(request.email())) {
            throw new ReglaNegocioException("Ya existe un cliente con el email: " + request.email());
        }
        if (clienteRepository.existsByDni(request.dni())) {
            throw new ReglaNegocioException("Ya existe un cliente con el DNI: " + request.dni());
        }
        Cliente cliente = Cliente.builder()
            .nombre(request.nombre())
            .apellido(request.apellido())
            .email(request.email())
            .telefono(request.telefono())
            .dni(request.dni())
            .build();
        return toResponse(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteDTO.Response actualizar(Long id, ClienteDTO.Request request) {
        Cliente cliente = buscarOFallar(id);
        clienteRepository.findByEmail(request.email())
            .filter(c -> !c.getId().equals(id))
            .ifPresent(c -> { throw new ReglaNegocioException("Email ya registrado por otro cliente"); });
        cliente.setNombre(request.nombre());
        cliente.setApellido(request.apellido());
        cliente.setEmail(request.email());
        cliente.setTelefono(request.telefono());
        return toResponse(clienteRepository.save(cliente));
    }

    @Transactional
    public void desactivar(Long id) {
        Cliente cliente = buscarOFallar(id);
        cliente.setActivo(false);
        clienteRepository.save(cliente);
    }

    private Cliente buscarOFallar(Long id) {
        return clienteRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", id));
    }

    private ClienteDTO.Response toResponse(Cliente c) {
        return new ClienteDTO.Response(
            c.getId(), c.getNombre(), c.getApellido(), c.getEmail(),
            c.getTelefono(), c.getDni(), c.getActivo(), c.getCreadoEn());
    }
}
