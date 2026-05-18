package com.salon.fiestas.controller;

import com.salon.fiestas.model.dto.ImagenContenidoDTO;
import com.salon.fiestas.model.dto.ImagenSalonDTO;
import com.salon.fiestas.service.ImagenSalonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/salones/{salonId}/imagenes")
@RequiredArgsConstructor
@Tag(name = "Imágenes de salón", description = "CRUD de imágenes con almacenamiento binario en base de datos")
public class ImagenSalonController {

    private final ImagenSalonService imagenSalonService;

    @GetMapping
    @Operation(summary = "Listar imágenes activas del salón (metadatos)")
    public List<ImagenSalonDTO.Response> listar(@PathVariable Long salonId) {
        return imagenSalonService.listar(salonId);
    }

    @GetMapping("/{imagenId}")
    @Operation(summary = "Obtener metadatos de una imagen")
    public ImagenSalonDTO.Response obtener(
            @PathVariable Long salonId,
            @PathVariable Long imagenId) {
        return imagenSalonService.obtener(salonId, imagenId);
    }

    @GetMapping("/{imagenId}/archivo")
    @Operation(summary = "Descargar el archivo binario de la imagen")
    public ResponseEntity<byte[]> descargarArchivo(
            @PathVariable Long salonId,
            @PathVariable Long imagenId) {
        ImagenContenidoDTO contenido = imagenSalonService.obtenerContenido(salonId, imagenId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"" + contenido.nombreArchivo() + "\"")
            .contentType(MediaType.parseMediaType(contenido.contentType()))
            .body(contenido.contenido());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Subir imagen al salón")
    public ImagenSalonDTO.Response crear(
            @PathVariable Long salonId,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Integer orden,
            @RequestParam(required = false) Boolean esPrincipal) {
        return imagenSalonService.crear(salonId, archivo, descripcion, orden, esPrincipal);
    }

    @PutMapping(value = "/{imagenId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Actualizar metadatos y/o reemplazar archivo de imagen")
    public ImagenSalonDTO.Response actualizar(
            @PathVariable Long salonId,
            @PathVariable Long imagenId,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Integer orden,
            @RequestParam(required = false) Boolean esPrincipal) {
        return imagenSalonService.actualizar(salonId, imagenId, archivo, descripcion, orden, esPrincipal);
    }

    @DeleteMapping("/{imagenId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desactivar imagen del salón")
    public void eliminar(
            @PathVariable Long salonId,
            @PathVariable Long imagenId) {
        imagenSalonService.eliminar(salonId, imagenId);
    }
}
