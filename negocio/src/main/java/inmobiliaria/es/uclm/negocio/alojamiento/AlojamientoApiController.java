package inmobiliaria.es.uclm.negocio.alojamiento;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import inmobiliaria.es.uclm.negocio.alojamiento.dto.AlojamientoSearchResultDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST que expone los endpoints para la gestión y
 * búsqueda de Alojamientos.
 * Toda la API de esta clase se sirve bajo la ruta /api/alojamientos.
 */
@RestController
@RequestMapping("/api/alojamientos")
public class AlojamientoApiController {

        // Inyección de la capa de servicio que contiene la lógica de negocio.
        @Autowired
        private AlojamientoService_Interfaz alojamientoService;

        /**
         * Endpoint principal (GET /) para la búsqueda y filtrado de alojamientos.
         * Acepta múltiples parámetros de consulta opcionales para refinar los
         * resultados.
         *
         * @param ciudad    (q) Filtro opcional por ciudad o término de búsqueda.
         * @param maxPrice  Filtro opcional para el precio máximo.
         * @param minRating Filtro opcional para la valoración mínima.
         * @param types     Filtro opcional de lista de tipos de alojamiento.
         * @param capacity  Filtro de capacidad (default 1 si no se especifica).
         * @param sortBy    Criterio de ordenación (default 'recommend').
         * @return Una lista de AlojamientoSearchResultDTO, nunca la entidad
         *         directamente.
         */
        @GetMapping
        public List<AlojamientoSearchResultDTO> buscarAlojamientosConFiltros(

                        // Los @RequestParam(required = false) permiten que todos los filtros
                        // sean opcionales. Si un parámetro no se envía, su valor será 'null'.
                        @RequestParam(value = "q", required = false) String ciudad,
                        @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
                        @RequestParam(value = "minRating", required = false) Double minRating,
                        @RequestParam(value = "types", required = false) List<String> types,
                        @RequestParam(value = "capacity", required = false, defaultValue = "1") int capacity,
                        @RequestParam(value = "sortBy", required = false, defaultValue = "recommend") String sortBy) {
                // 1. Delegación: La responsabilidad de la lógica de negocio (cómo filtrar)
                // se pasa completamente a la capa de servicio.
                List<Alojamiento> alojamientosEncontrados = alojamientoService.buscarConFiltros(
                                ciudad, maxPrice, minRating, types, capacity, sortBy);
                // 2. Transformación (Patrón DTO):
                // Mapeamos la lista de Entidades (Alojamiento) a una lista de DTOs.
                // Esto desacopla la API de la persistencia y previene problemas de
                // serialización (LazyInitializationExceptions o bucles infinitos).
                return alojamientosEncontrados.stream()
                                .map(AlojamientoSearchResultDTO::fromEntity) // Conversión estática
                                .collect(Collectors.toList());
        }
}