package com.salon.fiestas.controller;

import com.salon.fiestas.model.dto.PagoDTO;
import com.salon.fiestas.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Gestión de pagos de reservas")
public class PagoController {

    private final PagoService pagoService;

    @GetMapping("/reserva/{reservaId}")
    @Operation(summary = "Listar pagos de una reserva")
    public List<PagoDTO.Response> listarPorReserva(@PathVariable Long reservaId) {
        return pagoService.listarPorReserva(reservaId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pago por ID")
    public PagoDTO.Response obtener(@PathVariable Long id) {
        return pagoService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar un pago")
    public PagoDTO.Response registrar(@Valid @RequestBody PagoDTO.Request request) {
        return pagoService.registrar(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Anular un pago")
    public void anular(@PathVariable Long id) {
        pagoService.anular(id);
    }
}