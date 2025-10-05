package alquiler.negocio.repository;


import alquiler.negocio.entity.Alojamiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long> {
}
