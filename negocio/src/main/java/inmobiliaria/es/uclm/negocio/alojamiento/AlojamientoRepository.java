package inmobiliaria.es.uclm.negocio.alojamiento;
import org.springframework.data.jpa.repository.JpaRepository;


import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // <-- 1. Importa esto

public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long>, JpaSpecificationExecutor<Alojamiento> {

    // Ejemplos de consultas personalizadas:

    // Buscar por ciudad
    List<Alojamiento> findByCiudad(String ciudad);

    // Buscar por precio máximo
    List<Alojamiento> findByPrecioLessThan(BigDecimal precio);

    // Buscar por ciudad y precio máximo
    List<Alojamiento> findByCiudadAndPrecioLessThan(String ciudad, Double precio);

    

}
