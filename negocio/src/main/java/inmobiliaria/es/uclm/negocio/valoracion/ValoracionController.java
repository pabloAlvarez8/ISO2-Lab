package inmobiliaria.es.uclm.negocio.valoracion;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

    @PostMapping("/guardar")
    // CAMBIO AQUÍ: En vez de <?> ponemos <Map<String, Object>>
    public ResponseEntity<Map<String, Object>> guardar(@RequestBody Map<String, Object> payload) {

        try {
            Long inmuebleId = ((Number) payload.get("inmuebleId")).longValue();
            Long usuarioId = ((Number) payload.get("usuarioId")).longValue();
            Double puntuacion = ((Number) payload.get("puntuacion")).doubleValue();
            String comentario = (String) payload.get("comentario");

            Map<String, Object> resultado = service.guardarValoracion(inmuebleId, usuarioId, puntuacion, comentario);

            return ResponseEntity.ok(resultado);

        } catch (RuntimeException e) {
            // Asegúrate de devolver también un Map en el error para que coincida con el tipo
            return ResponseEntity.status(403).body(java.util.Map.of("error", e.getMessage()));
        }
    }
}