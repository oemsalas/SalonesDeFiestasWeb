package com.salon.fiestas.service;

import com.salon.fiestas.exception.RecursoNoEncontradoException;
import com.salon.fiestas.exception.ReglaNegocioException;
import com.salon.fiestas.model.dto.ReservaDTO;
import com.salon.fiestas.model.entity.*;
import com.salon.fiestas.model.enums.EstadoReserva;
import com.salon.fiestas.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final SalonRepository salonRepository;
    private final ServicioRepository servicioRepository;
    private final DisponibilidadService disponibilidadService;

    @Transactional(readOnly = true)
    public List<ReservaDTO.Response> listarPorCliente(Long clienteId) {
        return reservaRepository.findByClienteId(clienteId)
            .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ReservaDTO.Response obtener(Long id) {
        return toResponse(buscarOFallar(id));
    }

    @Transactional
    public ReservaDTO.Response crear(ReservaDTO.Request request) {
        // 1. Validar disponibilidad (lanza excepción si hay conflicto)
        disponibilidadService.verificarDisponibilidad(
            request.salonId(), request.fechaEvento(),
            request.horaInicio(), request.horaFin(), null);

        Cliente cliente = clienteRepository.findById(request.clienteId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", request.clienteId()));
        Salon salon = salonRepository.findById(request.salonId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Salón", request.salonId()));

        // 2. Calcular precio
        long horas = java.time.Duration.between(request.horaInicio(), request.horaFin()).toHours();
        BigDecimal precioBase = salon.getPrecioHora().multiply(BigDecimal.valueOf(horas));

        Reserva reserva = Reserva.builder()
            .cliente(cliente)
            .salon(salon)
            .fechaEvento(request.fechaEvento())
            .horaInicio(request.horaInicio())
            .horaFin(request.horaFin())
            .observaciones(request.observaciones())
            .precioTotal(precioBase)
            .build();

        // 3. Agregar servicios
        List<ReservaServicio> items = new ArrayList<>();
        if (request.servicios() != null) {
            for (var item : request.servicios()) {
                Servicio servicio = servicioRepository.findById(item.servicioId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Servicio", item.servicioId()));
                if (!Boolean.TRUE.equals(servicio.getActivo())) {
                    throw new ReglaNegocioException("El servicio '%s' no está disponible".formatted(servicio.getNombre()));
                }
                BigDecimal subtotal = servicio.getPrecio().multiply(BigDecimal.valueOf(item.cantidad()));
                reserva.setPrecioTotal(reserva.getPrecioTotal().add(subtotal));

                items.add(ReservaServicio.builder()
                    .id(new ReservaServicio.ReservaServicioId(null, servicio.getId()))
                    .reserva(reserva)
                    .servicio(servicio)
                    .cantidad(item.cantidad())
                    .precioUnitario(servicio.getPrecio())
                    .build());
            }
        }
        reserva.setServicios(items);

        return toResponse(reservaRepository.save(reserva));
    }

    @Transactional
    public ReservaDTO.Response confirmar(Long id) {
        Reserva reserva = buscarOFallar(id);
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new ReglaNegocioException("Solo se pueden confirmar reservas en estado PENDIENTE");
        }
        // Re-verificar disponibilidad al confirmar (protección contra race condition)
        disponibilidadService.verificarDisponibilidad(
            reserva.getSalon().getId(), reserva.getFechaEvento(),
            reserva.getHoraInicio(), reserva.getHoraFin(), reserva.getId());

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return toResponse(reservaRepository.save(reserva));
    }

    @Transactional
    public ReservaDTO.Response cancelar(Long id) {
        Reserva reserva = buscarOFallar(id);
        if (reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new ReglaNegocioException("No se puede cancelar una reserva ya finalizada");
        }
        reserva.setEstado(EstadoReserva.CANCELADA);
        return toResponse(reservaRepository.save(reserva));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Reserva buscarOFallar(Long id) {
        return reservaRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Reserva", id));
    }

    private ReservaDTO.Response toResponse(Reserva r) {
        return new ReservaDTO.Response(
            r.getId(),
            r.getCliente().getId(),
            r.getCliente().getNombre() + " " + r.getCliente().getApellido(),
            r.getSalon().getId(),
            r.getSalon().getNombre(),
            r.getFechaEvento(),
            r.getHoraInicio(),
            r.getHoraFin(),
            r.getEstado(),
            r.getPrecioTotal(),
            r.getSeñaPagada(),
            r.getObservaciones(),
            r.getCreadoEn()
        );
    }
}
