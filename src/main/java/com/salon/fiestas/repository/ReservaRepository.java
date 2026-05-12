package com.salon.fiestas.repository;

import com.salon.fiestas.model.entity.Reserva;
import com.salon.fiestas.model.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByClienteId(Long clienteId);

    List<Reserva> findBySalonIdAndFechaEvento(Long salonId, LocalDate fecha);

    @Query("""
        SELECT r FROM Reserva r
        WHERE r.salon.id = :salonId
          AND r.fechaEvento = :fecha
          AND r.estado IN ('PENDIENTE', 'CONFIRMADA')
          AND r.horaInicio < :horaFin
          AND r.horaFin > :horaInicio
          AND (:excludeId IS NULL OR r.id != :excludeId)
        """)
    List<Reserva> findSolapamientos(
        @Param("salonId") Long salonId,
        @Param("fecha") LocalDate fecha,
        @Param("horaInicio") LocalTime horaInicio,
        @Param("horaFin") LocalTime horaFin,
        @Param("excludeId") Long excludeId
    );

    List<Reserva> findBySalonIdAndEstadoIn(Long salonId, List<EstadoReserva> estados);
}
