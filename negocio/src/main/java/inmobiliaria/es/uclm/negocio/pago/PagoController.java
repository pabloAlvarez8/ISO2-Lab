package inmobiliaria.es.uclm.negocio.pago;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para simular pagos con tarjeta y PayPal.
 */
@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoServicio pagoServicio;
    private final String modo;

    public PagoController(PagoServicio pagoServicio,
                          @Value("${pagos.modo:SIMULADO}") String modo) {
        this.pagoServicio = pagoServicio;
        this.modo = modo;
    }

    private void verificarModoSimulado() {
        if (!"SIMULADO".equalsIgnoreCase(modo)) {
            throw new IllegalStateException("El sistema no está en modo SIMULADO");
        }
    }

    // ---------- TARJETA ----------

    @PostMapping("/tarjeta/crear")
    public ResponseEntity<Map<String, Object>> crearPagoTarjeta(@RequestBody Map<String, Object> body) {
        verificarModoSimulado();
        long importe = Long.parseLong(body.getOrDefault("importe", 1999).toString());
        String moneda = body.getOrDefault("moneda", "eur").toString();
        Map<String, Object> resp = pagoServicio.crearPagoTarjeta(importe, moneda);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/tarjeta/confirmar")
    public ResponseEntity<Map<String, Object>> confirmarPagoTarjeta(@RequestBody Map<String, Object> body) {
        verificarModoSimulado();
        String clientSecret = body.get("clientSecret").toString();
        boolean forzarFallo = Boolean.parseBoolean(body.getOrDefault("forzarFallo", "false").toString());
        Map<String, Object> resp = pagoServicio.confirmarPagoTarjeta(clientSecret, forzarFallo);
        return ResponseEntity.ok(resp);
    }

    // ---------- PAYPAL ----------

    @PostMapping("/paypal/crear")
    public ResponseEntity<Map<String, Object>> crearOrdenPayPal(@RequestBody Map<String, Object> body) {
        verificarModoSimulado();
        long importe = Long.parseLong(body.getOrDefault("importe", 1999).toString());
        String moneda = body.getOrDefault("moneda", "EUR").toString();
        Map<String, Object> resp = pagoServicio.crearOrdenPayPal(importe, moneda);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/paypal/capturar")
    public ResponseEntity<Map<String, Object>> capturarOrdenPayPal(@RequestBody Map<String, Object> body) {
        verificarModoSimulado();
        String idOrden = body.get("idOrden").toString();
        Map<String, Object> resp = pagoServicio.capturarOrdenPayPal(idOrden);
        return ResponseEntity.ok(resp);
    }
}
