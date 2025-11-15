package inmobiliaria.es.uclm.negocio.pago;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio que simula pagos con tarjeta (crédito/débito) y PayPal.
 * No hay dinero real, todo se guarda en memoria.
 */
@Service
public class PagoServicio {

    private final Map<String, Map<String, Object>> pagos = new ConcurrentHashMap<>();

    // ---------- TARJETA ----------

    public Map<String, Object> crearPagoTarjeta(long importeCents, String moneda) {
        String id = "pi_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        String clientSecret = "sim_client_" + UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> pago = new HashMap<>();

        pago.put("id", id);
        pago.put("clientSecret", clientSecret);
        pago.put("importe", importeCents);
        pago.put("moneda", moneda);
        pago.put("estado", "requiere_metodo_pago");
        pago.put("creado_en", Instant.now().toString());

        pagos.put(id, pago);
        return Map.copyOf(pago);
    }

    public Map<String, Object> confirmarPagoTarjeta(String clientSecret, boolean forzarFallo) {
        var entrada = pagos.values().stream()
                .filter(m -> clientSecret.equals(m.get("clientSecret")))
                .findFirst();

        if (entrada.isEmpty()) {
            throw new IllegalArgumentException("clientSecret desconocido");
        }

        Map<String, Object> pago = entrada.get();
        if (forzarFallo) {
            pago.put("estado", "rechazado");
            pago.put("error", "Tarjeta rechazada (simulado)");
        } else {
            pago.put("estado", "pagado");
            pago.put("confirmado_en", Instant.now().toString());
        }

        return Map.copyOf(pago);
    }

    // ---------- PAYPAL ----------

    public Map<String, Object> crearOrdenPayPal(long importeCents, String moneda) {
        String idOrden = "ORDEN-" + UUID.randomUUID().toString().substring(0, 12);
        Map<String, Object> orden = new HashMap<>();
        orden.put("id", idOrden);
        orden.put("estado", "CREADA");
        orden.put("importe", String.format("%.2f", importeCents / 100.0));
        orden.put("moneda", moneda.toUpperCase());
        orden.put("creada_en", Instant.now().toString());
        pagos.put(idOrden, orden);
        return Map.copyOf(orden);
    }

    public Map<String, Object> capturarOrdenPayPal(String idOrden) {
        Map<String, Object> orden = pagos.get(idOrden);
        if (orden == null)
            throw new IllegalArgumentException("idOrden desconocido");
        orden.put("estado", "COMPLETADA");
        orden.put("capturada_en", Instant.now().toString());
        return Map.copyOf(orden);
    }
}
