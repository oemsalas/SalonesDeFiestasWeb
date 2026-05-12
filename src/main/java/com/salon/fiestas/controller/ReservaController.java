package com.salon.fiestas.controller;

import com.salon.fiestas.model.dto.ReservaDTO;
import com.salon.fiestas.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Gestión de reservas del salón")
public class ReservaController {

    private final ReservaService reservaService;

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar reservas de un cliente")
    public List<ReservaDTO.Response> listarPorCliente(@PathVariable Long clienteId) {
        return reservaService.listarPorCliente(clienteId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reserva por ID")
    public ReservaDTO.Response obtener(@PathVariable Long id) {
        return reservaService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear nueva reserva — valida disponibilidad automáticamente")
    public ReservaDTO.Response crear(@Valid @RequestBody ReservaDTO.Request request) {
        return reservaService.crear(request);
    }

    @PostMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar una reserva pendiente")
    public ReservaDTO.Response confirmar(@PathVariable Long id) {
        return reservaService.confirmar(id);
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una reserva")
    public ReservaDTO.Response cancelar(@PathVariable Long id) {
        return reservaService.cancelar(id);
    }
}
