package com.salon.fiestas.service;

import com.salon.fiestas.exception.RecursoNoEncontradoException;
import com.salon.fiestas.exception.ReglaNegocioException;
import com.salon.fiestas.model.dto.PagoDTO;
import com.salon.fiestas.model.entity.Pago;
import com.salon.fiestas.model.entity.Reserva;
import com.salon.fiestas.model.enums.EstadoReserva;
import com.salon.fiestas.model.enums.TipoPago;
import com.salon.fiestas.repository.PagoRepository;
import com.salon.fiestas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;

    @Transactional(readOnly = true)
    public List<PagoDTO.Response> listarPorReserva(Long reservaId) {
        buscarReservaOFallar(reservaId);
        return pagoRepository.findByReservaId(reservaId)
            .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PagoDTO.Response obtener(Long id) {
        return toResponse(buscarPagoOFallar(id));
    }

    @Transactional
    public PagoDTO.Response registrar(PagoDTO.Request request) {
        Reserva reserva = buscarReservaOFallar(request.reservaId());

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ReglaNegocioException("No se pueden registrar pagos en una reserva cancelada");
        }
        if (reserva.getEstado() == EstadoReserva.FINALIZADA && request.tipo() != TipoPago.REEMBOLSO) {
            throw new ReglaNegocioException("La reserva ya está finalizada; solo se permiten reembolsos");
        }

        // Validar que la seña no supere el total
        if (request.tipo() == TipoPago.SEÑA) {
            BigDecimal señaAcumulada = reserva.getSeñaPagada().add(request.monto());
            if (señaAcumulada.compareTo(reserva.getPrecioTotal()) > 0) {
                throw new ReglaNegocioException(
                    "La seña acumulada ($%s) superaría el precio total ($%s)"
                    .formatted(señaAcumulada, reserva.getPrecioTotal()));
            }
            reserva.setSeñaPagada(señaAcumulada);
        }

        // Si el total pagado cubre el precio, pasar a FINALIZADA
        BigDecimal totalPagado = pagoRepository.findByReservaId(reserva.getId()).stream()
            .filter(p -> p.getTipo() != TipoPago.REEMBOLSO)
            .map(Pago::getMonto)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .add(request.tipo() != TipoPago.REEMBOLSO ? request.monto() : BigDecimal.ZERO);

        if (totalPagado.compareTo(reserva.getPrecioTotal()) >= 0
                && reserva.getEstado() == EstadoReserva.CONFIRMADA) {
            reserva.setEstado(EstadoReserva.FINALIZADA);
        }

        reservaRepository.save(reserva);

        Pago pago = Pago.builder()
            .reserva(reserva)
            .fechaPago(request.fechaPago())
            .monto(request.monto())
            .metodo(request.metodo())
            .tipo(request.tipo())
            .comprobante(request.comprobante())
            .build();

        return toResponse(pagoRepository.save(pago));
    }

    @Transactional
    public void anular(Long id) {
        Pago pago = buscarPagoOFallar(id);
        Reserva reserva = pago.getReserva();

        if (pago.getTipo() == TipoPago.SEÑA) {
            reserva.setSeñaPagada(
                reserva.getSeñaPagada().subtract(pago.getMonto()).max(BigDecimal.ZERO));
            reservaRepository.save(reserva);
        }

        pagoRepository.delete(pago);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Reserva buscarReservaOFallar(Long id) {
        return reservaRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Reserva", id));
    }

    private Pago buscarPagoOFallar(Long id) {
        return pagoRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Pago", id));
    }

    private PagoDTO.Response toResponse(Pago p) {
        return new PagoDTO.Response(
            p.getId(), p.getReserva().getId(), p.getFechaPago(),
            p.getMonto(), p.getMetodo(), p.getTipo(), p.getComprobante());
    }
}