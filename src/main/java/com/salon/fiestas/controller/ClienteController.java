package com.salon.fiestas.controller;

import com.salon.fiestas.model.dto.ClienteDTO;
import com.salon.fiestas.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gestión de clientes del salón")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    @Operation(summary = "Listar todos los clientes")
    public List<ClienteDTO.Response> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID")
    public ClienteDTO.Response obtener(@PathVariable Long id) {
        return clienteService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear nuevo cliente")
    public ClienteDTO.Response crear(@Valid @RequestBody ClienteDTO.Request request) {
        return clienteService.crear(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos del cliente")
    public ClienteDTO.Response actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteDTO.Request request) {
        return clienteService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desactivar cliente")
    public void desactivar(@PathVariable Long id) {
        clienteService.desactivar(id);
    }
}
