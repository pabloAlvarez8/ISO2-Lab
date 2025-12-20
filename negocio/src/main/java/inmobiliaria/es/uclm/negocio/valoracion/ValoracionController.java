package inmobiliaria.es.uclm.negocio.valoracion;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/valoraciones")
public class ValoracionController {
    private final ValoracionService service;

    public ValoracionController(ValoracionService service) {
        this.service = service;
    }

    @GetMapping("/inmueble/{id}")
    public List<ValoracionInmueble> listar(@PathVariable Long id) {
        return service.obtenerPorAlojamiento(id);
    }

    @PostMapping("/guardar") // Cambiamos nombre a 'guardar' para ser más semánticos
    public ResponseEntity<?> guardar(@RequestBody Map<String, Object> payload) {
        try {
            Long inmuebleId = ((Number) payload.get("inmuebleId")).longValue();
            Long usuarioId = ((Number) payload.get("usuarioId")).longValue();
            Double puntuacion = ((Number) payload.get("puntuacion")).doubleValue();
            String comentario = (String) payload.get("comentario");

            Map<String, Object> resultado = service.guardarValoracion(inmuebleId, usuarioId, puntuacion, comentario);
            return ResponseEntity.ok(resultado);
            
        } catch (RuntimeException e) {
            // Capturamos la excepción de "No ha visitado" y devolvemos Error 403 (Forbidden)
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                 .body(Map.of("mensaje", e.getMessage()));
        }
    }
}