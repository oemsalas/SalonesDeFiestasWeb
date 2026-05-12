package com.salon.fiestas.model.dto;

import com.salon.fiestas.model.enums.MetodoPago;
import com.salon.fiestas.model.enums.TipoPago;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PagoDTO {

    public record Request(
        @NotNull Long reservaId,
        @NotNull LocalDate fechaPago,
        @NotNull @DecimalMin("0.01") BigDecimal monto,
        @NotNull MetodoPago metodo,
        @NotNull TipoPago tipo,
        String comprobante
    ) {}

    public record Response(
        Long id,
        Long reservaId,
        LocalDate fechaPago,
        BigDecimal monto,
        MetodoPago metodo,
        TipoPago tipo,
        String comprobante
    ) {}
}