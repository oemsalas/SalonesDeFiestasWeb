package com.salon.fiestas.repository;

import com.salon.fiestas.model.entity.Salon;
import com.salon.fiestas.model.enums.EstadoSalon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface SalonRepository extends JpaRepository<Salon, Long> {

    List<Salon> findByEstado(EstadoSalon estado);

    @Query("""
        SELECT s FROM Salon s
        WHERE s.estado = 'ACTIVO'
          AND s.capacidadMax >= :capacidadMinima
          AND s.id NOT IN (
              SELECT r.salon.id FROM Reserva r
              WHERE r.fechaEvento = :fecha
                AND r.estado IN ('PENDIENTE', 'CONFIRMADA')
                AND r.horaInicio < :horaFin
                AND r.horaFin > :horaInicio
          )
          AND s.id NOT IN (
              SELECT b.salon.id FROM BloqueoSalon b
              WHERE :fecha BETWEEN b.fechaDesde AND b.fechaHasta
                AND (b.horaDesde IS NULL
                     OR (b.horaDesde < :horaFin AND b.horaHasta > :horaInicio))
          )
        """)
    List<Salon> findSalonesDisponibles(
        @Param("fecha") LocalDate fecha,
        @Param("horaInicio") LocalTime horaInicio,
        @Param("horaFin") LocalTime horaFin,
        @Param("capacidadMinima") int capacidadMinima
    );

    @Query("""
    SELECT s FROM Salon s
    WHERE s.estado = 'ACTIVO'
      AND s.latitud IS NOT NULL
      AND (6371 * acos(
            cos(radians(:lat)) * cos(radians(s.latitud)) *
            cos(radians(s.longitud) - radians(:lon)) +
            sin(radians(:lat)) * sin(radians(s.latitud))
          )) <= :radioKm
    ORDER BY (6371 * acos(
            cos(radians(:lat)) * cos(radians(s.latitud)) *
            cos(radians(s.longitud) - radians(:lon)) +
            sin(radians(:lat)) * sin(radians(s.latitud))
          )) ASC
    """)
    List<Salon> findSalonesCercanos(
        @Param("lat") double lat,
        @Param("lon") double lon,
        @Param("radioKm") double radioKm
    );
}
