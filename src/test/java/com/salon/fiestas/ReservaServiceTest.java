package com.salon.fiestas;

import com.salon.fiestas.exception.SalonNoDisponibleException;
import com.salon.fiestas.model.dto.ReservaDTO;
import com.salon.fiestas.model.entity.*;
import com.salon.fiestas.model.enums.EstadoReserva;
import com.salon.fiestas.repository.*;
import com.salon.fiestas.service.DisponibilidadService;
import com.salon.fiestas.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

//    @Mock ReservaRepository reservaRepository;
//    @Mock ClienteRepository clienteRepository;
//    @Mock SalonRepository salonRepository;
//    @Mock ServicioRepository servicioRepository;
//    @Mock DisponibilidadService disponibilidadService;
//
//    @InjectMocks ReservaService reservaService;
//
//    private Cliente cliente;
//    private Salon salon;
//
//    @BeforeEach
//    void setUp() {
//        cliente = Cliente.builder().id(1L).nombre("Ana").apellido("García")
//            .email("ana@test.com").telefono("1234").dni("12345678").build();
//
//        salon = Salon.builder().id(1L).nombre("Salón Principal")
//            .capacidadMax(200).precioHora(new BigDecimal("5000")).build();
//    }
//
//    @Test
//    void crear_deberiaCrearReservaCorrectamente() {
//        var request = new ReservaDTO.Request(1L, 1L,
//            LocalDate.now().plusDays(10),
//            LocalTime.of(18, 0), LocalTime.of(23, 0),
//            "Cumpleaños de 15", List.of());
//
//        Reserva reservaGuardada = Reserva.builder()
//            .id(1L).cliente(cliente).salon(salon)
//            .fechaEvento(request.fechaEvento())
//            .horaInicio(request.horaInicio()).horaFin(request.horaFin())
//            .estado(EstadoReserva.PENDIENTE)
//            .precioTotal(new BigDecimal("25000"))
//            .señaPagada(BigDecimal.ZERO).build();
//
//        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
//        when(salonRepository.findById(1L)).thenReturn(Optional.of(salon));
//        when(reservaRepository.save(any())).thenReturn(reservaGuardada);
//        doNothing().when(disponibilidadService)
//            .verificarDisponibilidad(anyLong(), any(), any(), any(), isNull());
//
//        ReservaDTO.Response response = reservaService.crear(request);
//
//        assertThat(response.id()).isEqualTo(1L);
//        assertThat(response.estado()).isEqualTo(EstadoReserva.PENDIENTE);
//        verify(disponibilidadService).verificarDisponibilidad(eq(1L),
//            eq(request.fechaEvento()), eq(request.horaInicio()), eq(request.horaFin()), isNull());
//    }
//
//    @Test
//    void crear_deberiaFallarSiSalonNoDisponible() {
//        var request = new ReservaDTO.Request(1L, 1L,
//            LocalDate.now().plusDays(5),
//            LocalTime.of(20, 0), LocalTime.of(23, 0), null, List.of());
//
//        doThrow(new SalonNoDisponibleException("ya existe una reserva que se solapa"))
//            .when(disponibilidadService).verificarDisponibilidad(any(), any(), any(), any(), any());
//
//        assertThatThrownBy(() -> reservaService.crear(request))
//            .isInstanceOf(SalonNoDisponibleException.class)
//            .hasMessageContaining("ya existe una reserva");
//    }
//
//    @Test
//    void confirmar_deberiaFallarSiNoEstaEnPendiente() {
//        Reserva reserva = Reserva.builder().id(1L).cliente(cliente).salon(salon)
//            .estado(EstadoReserva.CANCELADA)
//            .precioTotal(BigDecimal.TEN).señaPagada(BigDecimal.ZERO)
//            .fechaEvento(LocalDate.now().plusDays(1))
//            .horaInicio(LocalTime.of(18,0)).horaFin(LocalTime.of(22,0))
//            .build();
//        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
//
//        assertThatThrownBy(() -> reservaService.confirmar(1L))
//            .hasMessageContaining("PENDIENTE");
//    }
}
