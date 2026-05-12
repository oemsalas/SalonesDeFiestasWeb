package com.salon.fiestas.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "reserva_servicio")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReservaServicio {

    @EmbeddedId
    private ReservaServicioId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("reservaId")
    @JoinColumn(name = "reserva_id")
    @JsonIgnore   // ← agregá esta anotación
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("servicioId")
    @JoinColumn(name = "servicio_id")
    @JsonIgnore   // ← agregá esta anotación
    private Servicio servicio;

    @Column(nullable = false)
    @Builder.Default
    private Integer cantidad = 1;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Embeddable
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
    public static class ReservaServicioId implements Serializable {
        @Column(name = "reserva_id")
        private Long reservaId;

        @Column(name = "servicio_id")
        private Long servicioId;
    }
}
