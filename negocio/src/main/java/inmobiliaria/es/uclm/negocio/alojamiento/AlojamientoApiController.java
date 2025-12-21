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
 */
@RestController
@RequestMapping("/api/alojamientos")
public class AlojamientoApiController {

        // 1. Ponemos 'final' para asegurar que no cambia
        private final AlojamientoService alojamientoService;

        // 2. CONSTRUCTOR
        @Autowired
        public AlojamientoApiController(AlojamientoService alojamientoService) {
            this.alojamientoService = alojamientoService;
        }
        
        /**
         * Endpoint principal (GET /)
         */
        @GetMapping
        public List<AlojamientoSearchResultDTO> buscarAlojamientosConFiltros(
                        @RequestParam(value = "q", required = false) String ciudad,
                        @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
                        @RequestParam(value = "minRating", required = false) Double minRating,
                        @RequestParam(value = "types", required = false) List<String> types,
                        @RequestParam(value = "capacity", required = false, defaultValue = "1") int capacity,
                        @RequestParam(value = "sortBy", required = false, defaultValue = "recommend") String sortBy) {
                
                // Ahora esto SÍ funcionará porque alojamientoService ya no es null
                List<Alojamiento> alojamientosEncontrados = alojamientoService.buscarConFiltros(
                                ciudad, maxPrice, minRating, types, capacity, sortBy);

                return alojamientosEncontrados.stream()
                                .map(AlojamientoSearchResultDTO::fromEntity) 
                                .collect(Collectors.toList());
        }
}