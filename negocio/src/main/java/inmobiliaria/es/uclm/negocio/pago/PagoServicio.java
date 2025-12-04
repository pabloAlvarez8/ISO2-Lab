package inmobiliaria.es.uclm.negocio.pago;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio Mock para la simulación de pasarelas de pago.
 * Gestiona operaciones de Tarjeta y PayPal almacenando el estado en memoria (ConcurrentHashMap)
 * para evitar dependencias externas durante el desarrollo y pruebas.
 */
@Service
public class PagoServicio {

    // Almacén volátil de transacciones. Se perderá al reiniciar la aplicación.
    private final Map<String, Map<String, Object>> pagos = new ConcurrentHashMap<>();

    // tarjeta 

    /**
     * Inicializa un intento de pago con tarjeta simulando la respuesta de una API tipo Stripe.
     * Genera un `clientSecret` ficticio necesario para el flujo del frontend.
     *
     * @param importeCents Cantidad a cobrar en céntimos (ej: 1000 = 10.00).
     * @param moneda Código de la moneda (ej: "EUR").
     * @return Mapa con los datos del intento de pago (id, secret, estado inicial).
     */
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

    /**
     * Confirma y finaliza una transacción de tarjeta.
     * Permite forzar un error para probar la gestión de pagos fallidos en el cliente.
     *
     * @param clientSecret Token de seguridad generado en la creación del pago.
     * @param forzarFallo Si es true, simula que el banco ha rechazado la tarjeta.
     * @return Mapa con el estado actualizado del pago ("pagado" o "rechazado").
     * @throws IllegalArgumentException Si el clientSecret no existe en memoria.
     */
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

    // paypal

    /**
     * Crea una orden de pago simulada para el flujo de PayPal.
     *
     * @param importeCents Importe total en céntimos.
     * @param moneda Divisa de la transacción.
     * @return Datos de la orden creada con estado "CREADA".
     */
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

    /**
     * Simula la captura de fondos (Capture) una vez que el usuario ha aprobado el pago en PayPal.
     *
     * @param idOrden Identificador de la orden devuelto previamente.
     * @return Datos de la orden con el estado actualizado a "COMPLETADA".
     * @throws IllegalArgumentException Si el ID de la orden no se encuentra en memoria.
     */
    public Map<String, Object> capturarOrdenPayPal(String idOrden) {
        Map<String, Object> orden = pagos.get(idOrden);
        if (orden == null)
            throw new IllegalArgumentException("idOrden desconocido");
        orden.put("estado", "COMPLETADA");
        orden.put("capturada_en", Instant.now().toString());
        return Map.copyOf(orden);
    }
}