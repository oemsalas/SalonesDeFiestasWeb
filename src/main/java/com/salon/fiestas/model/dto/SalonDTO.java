package com.salon.fiestas.model.dto;

import com.salon.fiestas.model.enums.EstadoSalon;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class SalonDTO {

    public record Request(
        @NotBlank @Size(max = 150) String nombre,
        @NotNull @Min(1) Integer capacidadMax,
        String descripcion,
        @NotNull @DecimalMin("0.0") BigDecimal precioHora,
        String direccion,
        BigDecimal latitud,
        BigDecimal longitud
    ) {}

    public record Response(
        Long id,
        String nombre,
        Integer capacidadMax,
        String descripcion,
        BigDecimal precioHora,
        EstadoSalon estado,
        String direccion,
        BigDecimal latitud,
        BigDecimal longitud
    ) {}
}