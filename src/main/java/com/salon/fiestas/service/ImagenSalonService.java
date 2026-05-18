package com.salon.fiestas.service;

import com.salon.fiestas.config.ImagenSalonProperties;
import com.salon.fiestas.exception.RecursoNoEncontradoException;
import com.salon.fiestas.exception.ReglaNegocioException;
import com.salon.fiestas.model.dto.ImagenContenidoDTO;
import com.salon.fiestas.model.dto.ImagenSalonDTO;
import com.salon.fiestas.model.entity.ImagenSalon;
import com.salon.fiestas.model.entity.Salon;
import com.salon.fiestas.repository.ImagenSalonRepository;
import com.salon.fiestas.repository.SalonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ImagenSalonService {

    private final ImagenSalonRepository imagenSalonRepository;
    private final SalonRepository salonRepository;
    private final ImagenSalonProperties imagenProperties;

    @Transactional(readOnly = true)
    public List<ImagenSalonDTO.Response> listar(Long salonId) {
        verificarSalon(salonId);
        return imagenSalonRepository.findBySalonIdAndActivoTrueOrderByOrdenAsc(salonId).stream()
            .map(imagen -> toResponse(imagen, salonId))
            .toList();
    }

    @Transactional(readOnly = true)
    public ImagenSalonDTO.Response obtener(Long salonId, Long imagenId) {
        return toResponse(buscarImagen(salonId, imagenId), salonId);
    }

    @Transactional(readOnly = true)
    public ImagenContenidoDTO obtenerContenido(Long salonId, Long imagenId) {
        ImagenSalon imagen = buscarImagen(salonId, imagenId);
        if (imagen.getContenido() == null || imagen.getContenido().length == 0) {
            throw new RecursoNoEncontradoException("Contenido de imagen", imagenId);
        }
        return new ImagenContenidoDTO(
            imagen.getContenido(),
            imagen.getContentType(),
            imagen.getNombreArchivo()
        );
    }

    @Transactional
    public ImagenSalonDTO.Response crear(
            Long salonId,
            MultipartFile archivo,
            String descripcion,
            Integer orden,
            Boolean esPrincipal) {
        Salon salon = verificarSalon(salonId);
        ArchivoValidado validado = validarArchivo(archivo);
        boolean principal = Boolean.TRUE.equals(esPrincipal);

        if (principal) {
            imagenSalonRepository.quitarPrincipal(salonId);
        }

        ImagenSalon imagen = ImagenSalon.builder()
            .salon(salon)
            .nombreArchivo(validado.nombreArchivo())
            .contentType(validado.contentType())
            .tamanoBytes(validado.tamanoBytes())
            .contenido(validado.contenido())
            .descripcion(descripcion)
            .orden(orden != null ? orden : 0)
            .esPrincipal(principal)
            .build();

        return toResponse(imagenSalonRepository.save(imagen), salonId);
    }

    @Transactional
    public ImagenSalonDTO.Response actualizar(
            Long salonId,
            Long imagenId,
            MultipartFile archivo,
            String descripcion,
            Integer orden,
            Boolean esPrincipal) {
        ImagenSalon imagen = buscarImagen(salonId, imagenId);

        if (archivo != null && !archivo.isEmpty()) {
            ArchivoValidado validado = validarArchivo(archivo);
            imagen.setNombreArchivo(validado.nombreArchivo());
            imagen.setContentType(validado.contentType());
            imagen.setTamanoBytes(validado.tamanoBytes());
            imagen.setContenido(validado.contenido());
        }

        if (descripcion != null) {
            imagen.setDescripcion(descripcion);
        }
        if (orden != null) {
            imagen.setOrden(orden);
        }
        if (esPrincipal != null) {
            if (esPrincipal && !imagen.getEsPrincipal()) {
                imagenSalonRepository.quitarPrincipalExcepto(salonId, imagenId);
            }
            imagen.setEsPrincipal(esPrincipal);
        }

        return toResponse(imagenSalonRepository.save(imagen), salonId);
    }

    @Transactional
    public void eliminar(Long salonId, Long imagenId) {
        ImagenSalon imagen = buscarImagen(salonId, imagenId);
        imagen.setActivo(false);
        imagen.setEsPrincipal(false);
        imagenSalonRepository.save(imagen);
    }

    private Salon verificarSalon(Long salonId) {
        return salonRepository.findById(salonId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Salón", salonId));
    }

    private ImagenSalon buscarImagen(Long salonId, Long imagenId) {
        verificarSalon(salonId);
        return imagenSalonRepository.findByIdAndSalonId(imagenId, salonId)
            .filter(ImagenSalon::getActivo)
            .orElseThrow(() -> new RecursoNoEncontradoException("Imagen", imagenId));
    }

    private ArchivoValidado validarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ReglaNegocioException("El archivo de imagen es obligatorio");
        }

        String contentType = archivo.getContentType();
        if (contentType == null || contentType.isBlank()) {
            throw new ReglaNegocioException("No se pudo determinar el tipo de contenido del archivo");
        }

        String tipoNormalizado = contentType.toLowerCase(Locale.ROOT);
        if (!imagenProperties.allowedContentTypes().contains(tipoNormalizado)) {
            throw new ReglaNegocioException(
                "Tipo de archivo no permitido. Permitidos: " + imagenProperties.allowedContentTypes());
        }

        if (archivo.getSize() > imagenProperties.maxSizeBytes()) {
            throw new ReglaNegocioException(
                "El archivo supera el tamaño máximo de " + imagenProperties.maxSizeBytes() + " bytes");
        }

        String nombre = StringUtils.cleanPath(archivo.getOriginalFilename() != null
            ? archivo.getOriginalFilename()
            : "imagen");

        if (nombre.contains("..")) {
            throw new ReglaNegocioException("Nombre de archivo inválido");
        }

        try {
            return new ArchivoValidado(
                nombre,
                tipoNormalizado,
                archivo.getSize(),
                archivo.getBytes()
            );
        } catch (IOException e) {
            throw new ReglaNegocioException("No se pudo leer el archivo subido");
        }
    }

    private ImagenSalonDTO.Response toResponse(ImagenSalon imagen, Long salonId) {
        return new ImagenSalonDTO.Response(
            imagen.getId(),
            salonId,
            imagen.getNombreArchivo(),
            imagen.getContentType(),
            imagen.getTamanoBytes(),
            archivoUrl(salonId, imagen.getId()),
            imagen.getDescripcion(),
            imagen.getOrden(),
            imagen.getEsPrincipal(),
            imagen.getActivo(),
            imagen.getCreadoEn()
        );
    }

    private static String archivoUrl(Long salonId, Long imagenId) {
        return "/v1/salones/" + salonId + "/imagenes/" + imagenId + "/archivo";
    }

    private record ArchivoValidado(
        String nombreArchivo,
        String contentType,
        long tamanoBytes,
        byte[] contenido
    ) {}
}
