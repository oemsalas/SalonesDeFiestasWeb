package com.salon.fiestas.service;

import com.salon.fiestas.exception.RecursoNoEncontradoException;
import com.salon.fiestas.model.dto.ServicioDTO;
import com.salon.fiestas.model.entity.Servicio;
import com.salon.fiestas.model.enums.CategoriaServicio;
import com.salon.fiestas.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioService {

    private final ServicioRepository servicioRepository;

    @Transactional(readOnly = true)
    public List<ServicioDTO.Response> listar() {
        return servicioRepository.findAll()
            .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ServicioDTO.Response> listarActivos() {
        return servicioRepository.findByActivoTrue()
            .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ServicioDTO.Response> listarPorCategoria(CategoriaServicio categoria) {
        return servicioRepository.findByCategoriaAndActivoTrue(categoria)
            .stream().map(this::toResponse).toList();
    }

    @Transactional
    public ServicioDTO.Response crear(ServicioDTO.Request request) {
        Servicio s = Servicio.builder()
            .nombre(request.nombre())
            .descripcion(request.descripcion())
            .precio(request.precio())
            .categoria(request.categoria())
            .build();
        return toResponse(servicioRepository.save(s));
    }

    @Transactional
    public ServicioDTO.Response actualizar(Long id, ServicioDTO.Request request) {
        Servicio s = buscarOFallar(id);
        s.setNombre(request.nombre());
        s.setDescripcion(request.descripcion());
        s.setPrecio(request.precio());
        s.setCategoria(request.categoria());
        return toResponse(servicioRepository.save(s));
    }

    @Transactional
    public void desactivar(Long id) {
        Servicio s = buscarOFallar(id);
        s.setActivo(false);
        servicioRepository.save(s);
    }

    private Servicio buscarOFallar(Long id) {
        return servicioRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Servicio", id));
    }

    private ServicioDTO.Response toResponse(Servicio s) {
        return new ServicioDTO.Response(
            s.getId(), s.getNombre(), s.getDescripcion(),
            s.getPrecio(), s.getCategoria(), s.getActivo());
    }
}