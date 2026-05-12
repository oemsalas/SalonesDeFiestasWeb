package com.salon.fiestas.model.dto;

import com.salon.fiestas.model.enums.EstadoReserva;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ReservaDTO {

    public record Request(
        @NotNull Long clienteId,
        @NotNull Long salonId,
        @NotNull LocalDate fechaEvento,
        @NotNull LocalTime horaInicio,
        @NotNull LocalTime horaFin,
        String observaciones,
        List<ServicioItemRequest> servicios
    ) {}

    public record ServicioItemRequest(
        @NotNull Long servicioId,
        @Min(1) int cantidad
    ) {}

    public record Response(
        Long id,
        Long clienteId,
        String nombreCliente,
        Long salonId,
        String nombreSalon,
        LocalDate fechaEvento,
        LocalTime horaInicio,
        LocalTime horaFin,
        EstadoReserva estado,
        BigDecimal precioTotal,
        BigDecimal señaPagada,
        String observaciones,
        LocalDateTime creadoEn
    ) {}
}