package com.salon.fiestas.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "configuracion_salon")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConfiguracionSalon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salon_id", nullable = false, unique = true)
    private Salon salon;

    @Column(name = "min_horas_reserva", nullable = false)
    @Builder.Default
    private Integer minHorasReserva = 2;

    @Column(name = "max_horas_reserva", nullable = false)
    @Builder.Default
    private Integer maxHorasReserva = 12;

    @Column(name = "anticipacion_min_dias", nullable = false)
    @Builder.Default
    private Integer anticipacionMinDias = 1;

    @Column(name = "anticipacion_max_dias", nullable = false)
    @Builder.Default
    private Integer anticipacionMaxDias = 365;

    @Column(name = "permite_solapamiento", nullable = false)
    @Builder.Default
    private Boolean permiteSolapamiento = false;
}
