package inmobiliaria.es.uclm.negocio.alojamiento;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat; // IMPORTANTE: Necesario para las fechas
import inmobiliaria.es.uclm.negocio.alojamiento.dto.AlojamientoSearchResultDTO;

import java.math.BigDecimal;
import java.time.LocalDate; // IMPORTANTE
import java.util.List;

/**
 * Controlador REST que expone los endpoints para la gestión y
 * búsqueda de Alojamientos.
 * Toda la API de esta clase se sirve bajo la ruta /api/alojamientos.
 */
@RestController
@RequestMapping("/api/alojamientos")
public class AlojamientoApiController {

        private final AlojamientoService alojamientoService;

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

                // --- NUEVOS PARÁMETROS PARA FECHAS ---
                // Usamos @DateTimeFormat para entender formato YYYY-MM-DD
                @RequestParam(value = "checkin", required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkin,

                @RequestParam(value = "checkout", required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkout,
                // -------------------------------------

                @RequestParam(value = "sortBy", required = false, defaultValue = "recommend") String sortBy) {

                // Llamada actualizada al servicio pasando las fechas (ahora son 8 argumentos)
                List<Alojamiento> alojamientosEncontrados = alojamientoService.buscarConFiltros(
                        ciudad, maxPrice, minRating, types, capacity, checkin, checkout, sortBy);

                return alojamientosEncontrados.stream()
                        .map(AlojamientoSearchResultDTO::fromEntity)
                        .toList();
        }
}