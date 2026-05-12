package com.salon.fiestas.repository;
import com.salon.fiestas.model.entity.Servicio;
import com.salon.fiestas.model.enums.CategoriaServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    List<Servicio> findByActivoTrue();
    List<Servicio> findByCategoriaAndActivoTrue(CategoriaServicio categoria);
}
