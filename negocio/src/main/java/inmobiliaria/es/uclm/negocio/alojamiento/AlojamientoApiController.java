package inmobiliaria.es.uclm.negocio.alojamiento;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import inmobiliaria.es.uclm.negocio.alojamiento.dto.AlojamientoSearchResultDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST que expone la API pública para la consulta de alojamientos.
 * <p>
 * Gestiona las peticiones HTTP entrantes bajo la ruta '/api/alojamientos',
 * actuando como adaptador entre la interfaz web/móvil y la capa de servicios.
 * Se encarga de la validación básica de parámetros y la transformación de
 * entidades a DTOs.
 * </p>
 */
@RestController
@RequestMapping("/api/alojamientos")
public class AlojamientoApiController {

    /**
     * Servicio de negocio para la gestión de alojamientos.
     */
    @Autowired
    private AlojamientoService_Interfaz alojamientoService;

    /**
     * Endpoint de búsqueda avanzada de alojamientos (GET /).
     * <p>
     * Permite filtrar el catálogo de inmuebles mediante múltiples criterios opcionales.
     * La respuesta devuelve una lista de objetos de transferencia de datos (DTO)
     * para evitar exponer directamente la estructura de la base de datos.
     * </p>
     *
     * @param ciudad    Término de búsqueda textual (ciudad o ubicación). Mapeado al parámetro 'q'.
     * @param maxPrice  Tope de precio por noche permitido.
     * @param minRating Puntuación mínima requerida (0-5).
     * @param types     Lista de tipos de inmueble permitidos (ej. "Apartamento", "Casa").
     * @param capacity  Número mínimo de personas requeridas (por defecto 1).
     * @param sortBy    Criterio de ordenación de resultados (por defecto 'recommend').
     * @return Lista de {@link AlojamientoSearchResultDTO} con los resultados filtrados.
     */
    @GetMapping
    public List<AlojamientoSearchResultDTO> buscarAlojamientosConFiltros(
            @RequestParam(value = "q", required = false) String ciudad,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "types", required = false) List<String> types,
            @RequestParam(value = "capacity", required = false, defaultValue = "1") int capacity,
            @RequestParam(value = "sortBy", required = false, defaultValue = "recommend") String sortBy) {

        // Delegación de la lógica de filtrado a la capa de servicio.
        List<Alojamiento> alojamientosEncontrados = alojamientoService.buscarConFiltros(
                ciudad, maxPrice, minRating, types, capacity, sortBy);

        // Transformación a DTO:
        // Es vital convertir las entidades JPA a DTOs antes de devolverlas al cliente
        // para evitar problemas de serialización (Lazy Loading) y desacoplar la API.
        return alojamientosEncontrados.stream()
                .map(AlojamientoSearchResultDTO::fromEntity)
                .collect(Collectors.toList());
    }
}