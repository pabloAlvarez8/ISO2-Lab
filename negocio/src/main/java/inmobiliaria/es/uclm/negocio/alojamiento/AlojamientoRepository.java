package inmobiliaria.es.uclm.negocio.alojamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long>, JpaSpecificationExecutor<Alojamiento> {

    // BÚSQUEDAS MEJORADAS

    // 1. Buscar por ciudad ignorando mayúsculas/minúsculas y coincidencias parciales --> ejemplo: si buscas "bil", encontrará "Bilbao".
    List<Alojamiento> findByCiudadContainingIgnoreCase(String ciudad);

    // 2. Buscar por precio menor o igual al indicado
    // Usamos LessThanEqual para incluir el precio exacto (ej: si buscas 100€, que salgan los de 100€)
    List<Alojamiento> findByPrecioLessThanEqual(BigDecimal precio);

    // 3. Combinado: Ciudad (flexible) Y Precio máximo
    List<Alojamiento> findByCiudadContainingIgnoreCaseAndPrecioLessThanEqual(String ciudad, BigDecimal precio);

    // 4. Buscar alojamientos de un anfitrión (esto ya estaba bien, pero lo mantenemos)
    List<Alojamiento> findByAnfitrion_Id(Long idUsuario);

    @Query("SELECT MAX(a.precio) FROM Alojamiento a")
    BigDecimal findMaxPrecio();
}
