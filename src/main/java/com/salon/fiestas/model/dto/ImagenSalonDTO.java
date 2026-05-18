package com.salon.fiestas.model.dto;

import java.time.LocalDateTime;

public class ImagenSalonDTO {

    public record Response(
        Long id,
        Long salonId,
        String nombreArchivo,
        String contentType,
        long tamanoBytes,
        String archivoUrl,
        String descripcion,
        Integer orden,
        boolean esPrincipal,
        boolean activo,
        LocalDateTime creadoEn
    ) {}
}
