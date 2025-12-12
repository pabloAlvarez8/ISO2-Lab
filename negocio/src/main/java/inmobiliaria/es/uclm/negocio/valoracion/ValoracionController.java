package inmobiliaria.es.uclm.negocio.valoracion;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/valoraciones")
public class ValoracionController {
    @Autowired private ValoracionService service;

    @GetMapping("/inmueble/{id}")
    public List<ValoracionInmueble> listar(@PathVariable Long id) {
        return service.obtenerPorAlojamiento(id);
    }

    @PostMapping("/crear")
    public ValoracionInmueble crear(@RequestBody Map<String, Object> payload) {
        return service.crearValoracion(
            ((Number) payload.get("inmuebleId")).longValue(),
            ((Number) payload.get("usuarioId")).longValue(),
            ((Number) payload.get("puntuacion")).intValue(),
            (String) payload.get("comentario")
        );
    }
}