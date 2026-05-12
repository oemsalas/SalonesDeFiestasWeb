package com.salon.fiestas.controller;

import com.salon.fiestas.model.entity.HorarioSalon;
import com.salon.fiestas.model.entity.Salon;
import com.salon.fiestas.repository.HorarioSalonRepository;
import com.salon.fiestas.repository.SalonRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/v1/salones/{salonId}/horarios")
@RequiredArgsConstructor
@Tag(name = "Horarios", description = "Horarios operativos de salones")
public class HorarioSalonController {

    private final HorarioSalonRepository horarioRepo;
    private final SalonRepository salonRepo;

    public record HorarioRequest(
        int diaSemana,       // 0=Dom, 1=Lun ... 6=Sab
        LocalTime apertura,
        LocalTime cierre,
        boolean activo
    ) {}

    @GetMapping
    @Operation(summary = "Listar horarios del salón")
    public List<HorarioSalon> listar(@PathVariable Long salonId) {
        return horarioRepo.findBySalonId(salonId);
    }

    @PostMapping
@ResponseStatus(HttpStatus.CREATED)
@Operation(summary = "Agregar o actualizar horario operativo")
public HorarioSalon crear(@PathVariable Long salonId,
                           @RequestBody HorarioRequest req) {
    Salon salon = salonRepo.findById(salonId)
        .orElseThrow(() -> new RuntimeException("Salón no encontrado"));

    // Si ya existe ese día, actualizarlo en lugar de insertar
    HorarioSalon h = horarioRepo.findBySalonId(salonId).stream()
        .filter(x -> x.getDiaSemana().equals(req.diaSemana()))
        .findFirst()
        .orElse(HorarioSalon.builder().salon(salon).diaSemana(req.diaSemana()).build());

    h.setApertura(req.apertura());
    h.setCierre(req.cierre());
    h.setActivo(req.activo());

    return horarioRepo.save(h);
}

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar horario")
    public void eliminar(@PathVariable Long salonId, @PathVariable Long id) {
        horarioRepo.deleteById(id);
    }
}
