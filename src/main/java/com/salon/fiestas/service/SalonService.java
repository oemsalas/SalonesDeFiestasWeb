package com.salon.fiestas.service;

import com.salon.fiestas.exception.RecursoNoEncontradoException;
import com.salon.fiestas.model.dto.BloqueoDTO;
import com.salon.fiestas.model.dto.SalonDTO;
import com.salon.fiestas.model.dto.SalonDTO.Response;
import com.salon.fiestas.model.entity.BloqueoSalon;
import com.salon.fiestas.model.entity.Salon;
import com.salon.fiestas.model.enums.EstadoSalon;
import com.salon.fiestas.repository.BloqueoSalonRepository;
import com.salon.fiestas.repository.SalonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalonService {

    private final SalonRepository salonRepository;
    private final BloqueoSalonRepository bloqueoSalonRepository;

    @Transactional(readOnly = true)
    public List<SalonDTO.Response> listar() {
        return salonRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SalonDTO.Response obtener(Long id) {
        return toResponse(buscarOFallar(id));
    }

    @Transactional
    public SalonDTO.Response crear(SalonDTO.Request request) {
        Salon salon = Salon.builder()
            .nombre(request.nombre())
            .capacidadMax(request.capacidadMax())
            .descripcion(request.descripcion())
            .precioHora(request.precioHora())
            .direccion(request.direccion())
            .latitud(request.latitud())
            .longitud(request.longitud())
            .build();
        return toResponse(salonRepository.save(salon));
    }

    @Transactional
    public SalonDTO.Response actualizar(Long id, SalonDTO.Request request) {
        Salon salon = buscarOFallar(id);
        salon.setNombre(request.nombre());
        salon.setCapacidadMax(request.capacidadMax());
        salon.setDescripcion(request.descripcion());
        salon.setPrecioHora(request.precioHora());
        salon.setDireccion(request.direccion());
        salon.setLatitud(request.latitud());
        salon.setLongitud(request.longitud());
        return toResponse(salonRepository.save(salon));
    }

    @Transactional
    public void cambiarEstado(Long id, EstadoSalon estado) {
        Salon salon = buscarOFallar(id);
        salon.setEstado(estado);
        salonRepository.save(salon);
    }

    @Transactional
    public BloqueoDTO.Response crearBloqueo(BloqueoDTO.Request request) {
        Salon salon = buscarOFallar(request.salonId());
        BloqueoSalon bloqueo = BloqueoSalon.builder()
            .salon(salon)
            .fechaDesde(request.fechaDesde())
            .fechaHasta(request.fechaHasta())
            .horaDesde(request.horaDesde())
            .horaHasta(request.horaHasta())
            .motivo(request.motivo())
            .tipo(request.tipo())
            .build();
        BloqueoSalon saved = bloqueoSalonRepository.save(bloqueo);
        return toBloqueoResponse(saved);
    }

    @Transactional
    public void eliminarBloqueo(Long bloqueoId) {
        bloqueoSalonRepository.deleteById(bloqueoId);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Salon buscarOFallar(Long id) {
        return salonRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Salón", id));
    }

    private SalonDTO.Response toResponse(Salon s) {
        return new SalonDTO.Response(s.getId(), s.getNombre(),
            s.getCapacidadMax(), s.getDescripcion(), s.getPrecioHora(), s.getEstado(), s.getDireccion(), s.getLatitud(), s.getLongitud());
    }

    private BloqueoDTO.Response toBloqueoResponse(BloqueoSalon b) {
        return new BloqueoDTO.Response(b.getId(), b.getSalon().getId(),
            b.getFechaDesde(), b.getFechaHasta(), b.getHoraDesde(),
            b.getHoraHasta(), b.getMotivo(), b.getTipo());
    }   
    
    @Transactional(readOnly = true)
    public List<SalonDTO.Response> buscarCercanos(double lat, double lon, double radioKm) {
        return salonRepository.findSalonesCercanos(lat, lon, radioKm)
            .stream()
            .map(this::toResponse)
            .toList();
    }
}
