package com.salon.fiestas.repository;

import com.salon.fiestas.model.entity.ImagenSalon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ImagenSalonRepository extends JpaRepository<ImagenSalon, Long> {

    @Query("SELECT i FROM ImagenSalon i WHERE i.salon.id = :salonId AND i.activo = true ORDER BY i.orden ASC")
    List<ImagenSalon> findBySalonIdAndActivoTrueOrderByOrdenAsc(@Param("salonId") Long salonId);

    @Query("SELECT i FROM ImagenSalon i WHERE i.id = :id AND i.salon.id = :salonId")
    Optional<ImagenSalon> findByIdAndSalonId(@Param("id") Long id, @Param("salonId") Long salonId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ImagenSalon i SET i.esPrincipal = false WHERE i.salon.id = :salonId")
    void quitarPrincipal(@Param("salonId") Long salonId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ImagenSalon i SET i.esPrincipal = false WHERE i.salon.id = :salonId AND i.id <> :imagenId")
    void quitarPrincipalExcepto(@Param("salonId") Long salonId, @Param("imagenId") Long imagenId);
}
