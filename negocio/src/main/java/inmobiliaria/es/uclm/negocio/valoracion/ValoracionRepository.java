package inmobiliaria.es.uclm.negocio.valoracion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ValoracionRepository extends JpaRepository<ValoracionInmueble, Long> {
    List<ValoracionInmueble> findByInmuebleId(Long id);
}