package inmobiliaria.es.uclm.negocio.alojamiento;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController // <-- ¡Importante! @RestController = @Controller + @ResponseBody
@RequestMapping("/api/alojamientos") // <-- Esta URL coincide con tu 'fetch' de JavaScript
public class AlojamientoApiController {

    @Autowired
    private AlojamientoService_Interfaz alojamientoService; 
    // (Asegúrate de que AlojamientoService_Interfaz tenga el método buscarConFiltros)

    /**
     * API para buscar alojamientos con filtros dinámicos.
     * Esta es la función que tu JavaScript llamará.
     */
    @GetMapping
    public List<Alojamiento> buscarAlojamientosConFiltros(
        // Los @RequestParam coinciden con los 'params' de tu JS
        @RequestParam(value = "q", required = false, defaultValue = "") String ciudad,
        @RequestParam(value = "maxPrice", required = false, defaultValue = "1000000") double maxPrice,
        @RequestParam(value = "minRating", required = false, defaultValue = "0") double minRating,
        @RequestParam(value = "types", required = false) List<String> types,
        @RequestParam(value = "capacity", required = false, defaultValue = "1") int capacity,
        @RequestParam(value = "sortBy", required = false, defaultValue = "recommend") String sortBy
    ) {
        
        // (Opcional, pero recomendado: crear un objeto 'Filtros' para pasar esto)
        System.out.println("Buscando con ciudad: " + ciudad);
        System.out.println("Buscando con tipos: " + types);

        // Llama a tu servicio para que haga la consulta a MySQL
        return alojamientoService.buscarConFiltros(
            ciudad, maxPrice, minRating, types, capacity, sortBy
        );
    }
    
    // (En el futuro, podrías añadir más endpoints de API aquí, 
    //  como /api/alojamientos/{id} para obtener solo uno)
}