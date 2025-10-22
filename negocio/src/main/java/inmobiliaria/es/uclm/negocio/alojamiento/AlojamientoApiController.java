package inmobiliaria.es.uclm.negocio.alojamiento;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
// 1. Importa el DTO de búsqueda
import inmobiliaria.es.uclm.negocio.alojamiento.dto.AlojamientoSearchResultDTO; 

import java.math.BigDecimal; // 2. Importa BigDecimal
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alojamientos")
public class AlojamientoApiController {

    @Autowired
    private AlojamientoService_Interfaz alojamientoService; 

    @GetMapping
    // 3. Devuelve una lista del DTO de BÚSQUEDA
    public List<AlojamientoSearchResultDTO> buscarAlojamientosConFiltros(
            
            // 4. Arregla los tipos de parámetro.
            // Se quitan los 'defaultValue' para que sean 'null' si no se envían.
            @RequestParam(value = "q", required = false) String ciudad,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice, // <-- ARREGLADO
            @RequestParam(value = "minRating", required = false) Double minRating,   // <-- ARREGLADO
            @RequestParam(value = "types", required = false) List<String> types,
            @RequestParam(value = "capacity", required = false, defaultValue = "1") int capacity,
            @RequestParam(value = "sortBy", required = false, defaultValue = "recommend") String sortBy
    ) {
        
        // 5. Llama al servicio (ahora los tipos coinciden perfectamente)
        List<Alojamiento> alojamientosEncontrados = alojamientoService.buscarConFiltros(
            ciudad, maxPrice, minRating, types, capacity, sortBy
        );
        
        // 6. Convierte la lista de Entidades a una lista de DTOs
        // (Esto evita errores de LazyInitializationException y bucles infinitos)
        return alojamientosEncontrados.stream()
                .map(AlojamientoSearchResultDTO::fromEntity)
                .collect(Collectors.toList());
    }
}