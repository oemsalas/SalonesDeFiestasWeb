package com.salon.fiestas.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "horario_salon", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"salon_id", "dia_semana"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HorarioSalon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salon_id", nullable = false)
    @JsonIgnore   // ← agregá esta anotación
    private Salon salon;

    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana;

    @Column(nullable = false)
    private LocalTime apertura;

    @Column(nullable = false)
    private LocalTime cierre;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
