package com.salon.fiestas.model.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class ClienteDTO {

    public record Request(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 100) String apellido,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(max = 20) String telefono,
        @NotBlank @Size(max = 20) String dni
    ) {}

    public record Response(
        Long id,
        String nombre,
        String apellido,
        String email,
        String telefono,
        String dni,
        Boolean activo,
        LocalDateTime creadoEn
    ) {}
}