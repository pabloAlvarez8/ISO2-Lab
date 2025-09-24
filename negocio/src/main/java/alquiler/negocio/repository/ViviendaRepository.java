package alquiler.negocio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import alquiler.negocio.entity.Vivienda;

public interface ViviendaRepository extends JpaRepository<Vivienda, Long> {
}
