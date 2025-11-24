package inmobiliaria.es.uclm.negocio.alojamiento;

import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; 

/**
 * Repositorio de persistencia para la entidad {@link Alojamiento}.
 * <p>
 * Esta interfaz hereda de {@link JpaRepository} para proporcionar operaciones CRUD estándar
 * y paginación automática. Además, extiende {@link JpaSpecificationExecutor}, lo que habilita
 * el uso de la API de Criteria (Specifications) para construir consultas dinámicas y complejas
 * basadas en múltiples filtros opcionales (ciudad, precio, tipo, etc.) sin necesidad de
 * escribir SQL manual.
 * </p>
 */
public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long>, JpaSpecificationExecutor<Alojamiento> {

    /**
     * Recupera todos los alojamientos ubicados en una ciudad específica.
     * Se trata de una "Derived Query" donde Spring genera el SQL basándose en el nombre del método.
     * * @param ciudad Nombre exacto de la ciudad.
     * @return Lista de alojamientos en dicha ubicación.
     */
    List<Alojamiento> findByCiudad(String ciudad);

    /**
     * Busca alojamientos cuyo precio por noche sea estrictamente menor al indicado.
     * Útil para filtros de presupuesto máximo.
     * * @param precio Precio tope (no incluido).
     * @return Lista de alojamientos que cumplen el criterio económico.
     */
    List<Alojamiento> findByPrecioLessThan(BigDecimal precio);

    /**
     * Consulta combinada que filtra por ubicación exacta y un tope de precio.
     * Permite acotar la búsqueda cuando el usuario define ambos criterios obligatorios.
     * * @param ciudad Nombre de la ciudad.
     * @param precio Precio máximo permitido.
     * @return Lista de resultados que cumplen ambas condiciones simultáneamente.
     */
    List<Alojamiento> findByCiudadAndPrecioLessThan(String ciudad, BigDecimal precio);

}