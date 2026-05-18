package com.salon.fiestas.model.dto;

public record ImagenContenidoDTO(
    byte[] contenido,
    String contentType,
    String nombreArchivo
) {}
