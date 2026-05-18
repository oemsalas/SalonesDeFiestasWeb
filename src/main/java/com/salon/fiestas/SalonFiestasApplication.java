package com.salon.fiestas;

import com.salon.fiestas.config.ImagenSalonProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ImagenSalonProperties.class)
public class SalonFiestasApplication {
    public static void main(String[] args) {
        SpringApplication.run(SalonFiestasApplication.class, args);
    }
}
