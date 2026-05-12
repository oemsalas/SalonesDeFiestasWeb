package com.salon.fiestas.repository;

import com.salon.fiestas.model.entity.HorarioSalon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioSalonRepository extends JpaRepository<HorarioSalon, Long> {
    List<HorarioSalon> findBySalonId(Long salonId);
}