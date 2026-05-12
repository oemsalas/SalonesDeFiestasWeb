package com.salon.fiestas.model.dto;

import com.salon.fiestas.model.enums.TipoBloqueo;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class BloqueoDTO {

    public record Request(
        @NotNull Long salonId,
        @NotNull LocalDate fechaDesde,
        @NotNull LocalDate fechaHasta,
        LocalTime horaDesde,
        LocalTime horaHasta,
        String motivo,
        @NotNull TipoBloqueo tipo
    ) {}

    public record Response(
        Long id,
        Long salonId,
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        LocalTime horaDesde,
        LocalTime horaHasta,
        String motivo,
        TipoBloqueo tipo
    ) {}
}