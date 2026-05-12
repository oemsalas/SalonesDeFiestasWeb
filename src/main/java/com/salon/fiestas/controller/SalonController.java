package com.salon.fiestas.controller;

import com.salon.fiestas.model.dto.BloqueoDTO;
import com.salon.fiestas.model.dto.DisponibilidadDTO;
import com.salon.fiestas.model.dto.SalonDTO;
import com.salon.fiestas.model.enums.EstadoSalon;
import com.salon.fiestas.service.DisponibilidadService;
import com.salon.fiestas.service.SalonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/salones")
@RequiredArgsConstructor
@Tag(name = "Salones", description = "Gestión de salones y disponibilidad")
public class SalonController {

    private final SalonService salonService;    
    private final DisponibilidadService disponibilidadService;

    @GetMapping
    @Operation(summary = "Listar todos los salones")
    public List<SalonDTO.Response> listar() {
        return salonService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener salón por ID")
    public SalonDTO.Response obtener(@PathVariable Long id) {
        return salonService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear nuevo salón")
    public SalonDTO.Response crear(@Valid @RequestBody SalonDTO.Request request) {
        return salonService.crear(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar salón")
    public SalonDTO.Response actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SalonDTO.Request request) {
        return salonService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado del salón")
    public void cambiarEstado(@PathVariable Long id, @RequestParam EstadoSalon estado) {
        salonService.cambiarEstado(id, estado);
    }

    // ── Disponibilidad ────────────────────────────────────────────────────────

    @PostMapping("/disponibilidad")
    @Operation(summary = "Consultar salones disponibles para una fecha y horario")
    public List<DisponibilidadDTO.ConsultaResponse> consultarDisponibilidad(
            @Valid @RequestBody DisponibilidadDTO.ConsultaRequest request) {
        return disponibilidadService.consultarDisponibilidad(request);
    }

    // ── Bloqueos ──────────────────────────────────────────────────────────────

    @PostMapping("/bloqueos")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear bloqueo manual de un salón")
    public BloqueoDTO.Response crearBloqueo(@Valid @RequestBody BloqueoDTO.Request request) {
        return salonService.crearBloqueo(request);
    }

    @DeleteMapping("/bloqueos/{bloqueoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un bloqueo")
    public void eliminarBloqueo(@PathVariable Long bloqueoId) {
        salonService.eliminarBloqueo(bloqueoId);
    }

    @GetMapping("/cercanos")
    @Operation(summary = "Buscar salones cercanos a una coordenada")
    public List<SalonDTO.Response> cercanos(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "10") double radioKm) {
        return salonService.buscarCercanos(lat, lon, radioKm);
    }
}
