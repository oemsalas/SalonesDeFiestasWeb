package com.salon.fiestas.model.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class DisponibilidadDTO {

    public record ConsultaRequest(
        @NotNull LocalDate fecha,
        @NotNull LocalTime horaInicio,
        @NotNull LocalTime horaFin,
        @Min(1) int capacidadMinima
    ) {}

    public record ConsultaResponse(
        Long salonId,
        String nombreSalon,
        Integer capacidadMax,
        BigDecimal precioHora,
        boolean disponible,
        List<String> razonesBloqueado
    ) {}
}
