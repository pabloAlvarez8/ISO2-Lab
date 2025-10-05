package alquiler.negocio.repository;

import alquiler.negocio.entity.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {
    List<Disponibilidad> findByAlojamientoIdAndDisponibleTrue(Long alojamientoId);
    List<Disponibilidad> findByAlojamientoCiudadAndFechaBetweenAndDisponibleTrue(String ciudad, LocalDate desde, LocalDate hasta);
}
