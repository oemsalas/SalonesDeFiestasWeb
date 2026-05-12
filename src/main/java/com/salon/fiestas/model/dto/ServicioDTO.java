package com.salon.fiestas.model.dto;

import com.salon.fiestas.model.enums.CategoriaServicio;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ServicioDTO {

    public record Request(
        @NotBlank @Size(max = 150) String nombre,
        String descripcion,
        @NotNull @DecimalMin("0.0") BigDecimal precio,
        @NotNull CategoriaServicio categoria
    ) {}

    public record Response(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        CategoriaServicio categoria,
        Boolean activo
    ) {}
}