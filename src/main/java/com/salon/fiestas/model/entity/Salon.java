package com.salon.fiestas.model.entity;

import com.salon.fiestas.model.enums.EstadoSalon;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "salon")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Salon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(name = "capacidad_max", nullable = false)
    private Integer capacidadMax;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio_hora", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioHora;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(nullable = false, columnDefinition = "estado_salon")
    @Builder.Default
    private EstadoSalon estado = EstadoSalon.ACTIVO;

    @Column(name = "creado_en", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime creadoEn;

    @OneToOne(mappedBy = "salon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ConfiguracionSalon configuracion;

    @OneToMany(mappedBy = "salon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<HorarioSalon> horarios = new ArrayList<>();

    @OneToMany(mappedBy = "salon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BloqueoSalon> bloqueos = new ArrayList<>();

    @OneToMany(mappedBy = "salon", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Reserva> reservas = new ArrayList<>();

    @Column(length = 255)
    private String direccion;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitud;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitud;
}
