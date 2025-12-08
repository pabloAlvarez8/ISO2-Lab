package inmobiliaria.es.uclm.negocio.alojamiento;

import inmobiliaria.es.uclm.negocio.alojamiento.dto.DestinoDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de negocio para la gestión de alojamientos.
 * Define las operaciones principales para el mantenimiento, búsqueda y filtrado
 * de los inmuebles disponibles en el sistema.
 */
public interface AlojamientoService_Interfaz {

    /**
     * Recupera el listado completo de todos los alojamientos registrados.
     * * @return Lista con todos los objetos Alojamiento.
     */
    List<Alojamiento> listarTodos();

    /**
     * Persiste un alojamiento en la base de datos.
     * Sirve tanto para dar de alta uno nuevo como para actualizar los datos de uno existente.
     * * @param alojamiento El objeto con los datos a guardar.
     */
    void guardar(Alojamiento alojamiento);

    /**
     * Elimina un alojamiento del sistema de forma permanente.
     * * @param id Identificador único del alojamiento a borrar.
     */
    void eliminar(Long id);

    /**
     * Busca alojamientos que coincidan exactamente con el nombre de la ciudad.
     * * @param ciudad Nombre de la ciudad por la que filtrar.
     * @return Lista de alojamientos encontrados en esa ubicación.
     */
    List<Alojamiento> buscarPorCiudad(String ciudad);

    /**
     * Busca un alojamiento específico por su clave primaria.
     * * @param id Identificador del alojamiento.
     * @return Un Optional que contiene el alojamiento si existe, o vacío si no se encuentra.
     */
    Optional<Alojamiento> buscarPorId(Long id);

    /**
     * Genera un listado resumido de los destinos más frecuentes o populares.
     * Utiliza un DTO para devolver solo la información necesaria para la vista (imagen, nombre, recuento).
     * * @return Lista de objetos de transferencia con datos de destinos.
     */
    List<DestinoDTO> obtenerDestinosPopulares();

    /**
     * Realiza una búsqueda avanzada aplicando múltiples criterios de forma simultánea.
     * Los parámetros nulos se ignoran para no restringir la búsqueda por ese campo.
     * * @param ciudad Ciudad de destino (parcial o completa).
     * @param maxPrice Precio máximo por noche permitido.
     * @param minRating Puntuación mínima requerida.
     * @param types Lista de tipos de alojamiento (ej: "Apartamento", "Hotel").
     * @param capacity Capacidad mínima de personas.
     * @param sortBy Criterio de ordenación (ej: "price_asc", "rating_desc").
     * @return Lista de alojamientos que cumplen con todos los filtros activos.
     */
    List<Alojamiento> buscarConFiltros(
            String ciudad,
            BigDecimal maxPrice,
            Double minRating,
            List<String> types,
            int capacity,
            String sortBy);

            /**
     * Recupera todos los alojamientos que pertenecen a un anfitrión específico.
     * @param idUsuario ID del usuario anfitrión.
     * @return Lista de alojamientos de ese usuario.
     */
    List<Alojamiento> listarAlojamientosDeAnfitrion(Long idUsuario);
}