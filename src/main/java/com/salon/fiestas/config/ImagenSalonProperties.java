package com.salon.fiestas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "salon.imagenes")
public record ImagenSalonProperties(
    long maxSizeBytes,
    List<String> allowedContentTypes
) {}
