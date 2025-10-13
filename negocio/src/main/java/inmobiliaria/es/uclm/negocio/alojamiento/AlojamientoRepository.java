package inmobiliaria.es.uclm.negocio.alojamiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long> {

    // Ejemplos de consultas personalizadas:

    // Buscar por ciudad
    List<Alojamiento> findByCiudad(String ciudad);

    // Buscar por precio máximo
    List<Alojamiento> findByPrecioLessThan(Double precio);

    // Buscar por ciudad y precio máximo
    List<Alojamiento> findByCiudadAndPrecioLessThan(String ciudad, Double precio);

    

}
