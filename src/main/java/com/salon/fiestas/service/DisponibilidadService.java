package com.salon.fiestas.service;

import com.salon.fiestas.exception.ReglaNegocioException;
import com.salon.fiestas.exception.SalonNoDisponibleException;
import com.salon.fiestas.model.dto.DisponibilidadDTO;
import com.salon.fiestas.model.entity.ConfiguracionSalon;
import com.salon.fiestas.model.entity.Reserva;
import com.salon.fiestas.model.entity.Salon;
import com.salon.fiestas.repository.BloqueoSalonRepository;
import com.salon.fiestas.repository.HorarioSalonRepository;
import com.salon.fiestas.repository.ReservaRepository;
import com.salon.fiestas.repository.SalonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DisponibilidadService {

    private final SalonRepository salonRepository;
    private final ReservaRepository reservaRepository;
    private final HorarioSalonRepository horarioSalonRepository;
    private final BloqueoSalonRepository bloqueoSalonRepository;

    public List<DisponibilidadDTO.ConsultaResponse> consultarDisponibilidad(
            DisponibilidadDTO.ConsultaRequest request) {

        validarRangoHorario(request.horaInicio(), request.horaFin());

        return salonRepository.findSalonesDisponibles(
                request.fecha(),
                request.horaInicio(),
                request.horaFin(),
                request.capacidadMinima()
        ).stream().map(salon ->
            new DisponibilidadDTO.ConsultaResponse(
                salon.getId(), salon.getNombre(), salon.getCapacidadMax(),
                salon.getPrecioHora(), true, List.of())
        ).toList();
    }

    /**
     * Verifica que un salón específico esté disponible para el rango solicitado.
     * Lanza excepción si hay algún impedimento.
     */
    public void verificarDisponibilidad(Long salonId, LocalDate fecha,
            LocalTime horaInicio, LocalTime horaFin, Long excludeReservaId) {

        Salon salon = salonRepository.findById(salonId)
            .orElseThrow(() -> new ReglaNegocioException("Salón no encontrado"));

        List<String> razones = new ArrayList<>();

        // 1. Validar horario operativo
        int diaSemana = fecha.getDayOfWeek().getValue() % 7;
        horarioSalonRepository.findBySalonId(salonId).stream()
            .filter(h -> h.getDiaSemana().equals(diaSemana) && Boolean.TRUE.equals(h.getActivo()))
            .findFirst()
            .ifPresentOrElse(
                horario -> {
                    if (horaInicio.isBefore(horario.getApertura()) || horaFin.isAfter(horario.getCierre())) {
                        razones.add("fuera del horario operativo (%s - %s)"
                            .formatted(horario.getApertura(), horario.getCierre()));
                    }
                },
                () -> razones.add("el salón no opera ese día de la semana")
            );

        // 2. Verificar bloqueos manuales
        boolean bloqueado = bloqueoSalonRepository.findBySalonId(salonId).stream()
            .anyMatch(b -> {
                boolean rangoFecha = !fecha.isBefore(b.getFechaDesde()) && !fecha.isAfter(b.getFechaHasta());
                if (!rangoFecha) return false;
                if (b.getHoraDesde() == null) return true;
                return horaInicio.isBefore(b.getHoraHasta()) && horaFin.isAfter(b.getHoraDesde());
            });
        if (bloqueado) {
            razones.add("el salón tiene un bloqueo activo para ese rango");
        }

        // 3. Verificar solapamiento con otras reservas
        List<Reserva> solapamientos = reservaRepository.findSolapamientos(
            salonId, fecha, horaInicio, horaFin, excludeReservaId);
        if (!solapamientos.isEmpty()) {
            razones.add("ya existe una reserva confirmada que se solapa con el horario solicitado");
        }

        // 4. Validar reglas de configuración (si existen)
        ConfiguracionSalon config = salon.getConfiguracion();
        if (config != null) {
            long duracionHoras = java.time.Duration.between(horaInicio, horaFin).toHours();
            if (duracionHoras < config.getMinHorasReserva()) {
                razones.add("duración mínima requerida: %d hs".formatted(config.getMinHorasReserva()));
            }
            if (duracionHoras > config.getMaxHorasReserva()) {
                razones.add("duración máxima permitida: %d hs".formatted(config.getMaxHorasReserva()));
            }
            long diasAnticipacion = ChronoUnit.DAYS.between(LocalDate.now(), fecha);
            if (diasAnticipacion < config.getAnticipacionMinDias()) {
                razones.add("se requieren al menos %d días de anticipación"
                    .formatted(config.getAnticipacionMinDias()));
            }
            if (diasAnticipacion > config.getAnticipacionMaxDias()) {
                razones.add("no se puede reservar con más de %d días de anticipación"
                    .formatted(config.getAnticipacionMaxDias()));
            }
        }

        if (!razones.isEmpty()) {
            throw new SalonNoDisponibleException(String.join("; ", razones));
        }
    }

    private void validarRangoHorario(LocalTime inicio, LocalTime fin) {
        if (!fin.isAfter(inicio)) {
            throw new ReglaNegocioException("La hora de fin debe ser posterior a la hora de inicio");
        }
    }
}