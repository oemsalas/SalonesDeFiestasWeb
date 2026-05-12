package com.salon.fiestas.repository;

import com.salon.fiestas.model.entity.BloqueoSalon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloqueoSalonRepository extends JpaRepository<BloqueoSalon, Long> {
    List<BloqueoSalon> findBySalonId(Long salonId);
}