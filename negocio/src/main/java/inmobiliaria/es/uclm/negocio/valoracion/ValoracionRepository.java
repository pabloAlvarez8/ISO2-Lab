package inmobiliaria.es.uclm.negocio.valoracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ValoracionRepository extends JpaRepository<ValoracionInmueble, Long> {
    // Obtener lista ordenada por fecha (más recientes primero)
    List<ValoracionInmueble> findByInmuebleIdOrderByCreatedAtDesc(Long id);

    // Calcular la media
    @Query("SELECT AVG(v.puntuacion) FROM ValoracionInmueble v WHERE v.inmueble.id = :inmuebleId")
    Double obtenerMediaPuntuacion(@Param("inmuebleId") Long inmuebleId);
    Optional<ValoracionInmueble> findByUsuario_IdAndInmueble_Id(Long usuarioId, Long inmuebleId);
}