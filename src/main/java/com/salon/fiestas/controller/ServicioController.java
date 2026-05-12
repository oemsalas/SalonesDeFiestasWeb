package com.salon.fiestas.controller;

import com.salon.fiestas.model.dto.ServicioDTO;
import com.salon.fiestas.model.enums.CategoriaServicio;
import com.salon.fiestas.service.ServicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/servicios")
@RequiredArgsConstructor
@Tag(name = "Servicios", description = "Catálogo de servicios del salón")
public class ServicioController {

    private final ServicioService servicioService;

    @GetMapping
    @Operation(summary = "Listar todos los servicios")
    public List<ServicioDTO.Response> listar() {
        return servicioService.listar();
    }

    @GetMapping("/activos")
    @Operation(summary = "Listar servicios activos")
    public List<ServicioDTO.Response> listarActivos() {
        return servicioService.listarActivos();
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Listar servicios por categoría")
    public List<ServicioDTO.Response> listarPorCategoria(@PathVariable CategoriaServicio categoria) {
        return servicioService.listarPorCategoria(categoria);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear nuevo servicio")
    public ServicioDTO.Response crear(@Valid @RequestBody ServicioDTO.Request request) {
        return servicioService.crear(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar servicio")
    public ServicioDTO.Response actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ServicioDTO.Request request) {
        return servicioService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desactivar servicio")
    public void desactivar(@PathVariable Long id) {
        servicioService.desactivar(id);
    }
}
