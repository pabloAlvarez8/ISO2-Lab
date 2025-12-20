package inmobiliaria.es.uclm.negocio.pago;

import org.springframework.stereotype.Service;


@Service
public class PagoService {

    // LÓGICA DE TARJETA
    public boolean procesarPagoTarjeta(String numeroTarjeta, String caducidad, String cvv) {
        // Simulamos validaciones
        if (numeroTarjeta == null || numeroTarjeta.length() < 13) return false;

        // Simulamos conexión con Banco...
        // Aquí podrías meter tu lógica de "clientSecret" si quisieras,
        // pero para web directa basta con devolver un booleano.
        return true; // Pago aceptado
    }

    // LÓGICA DE PAYPAL
    public boolean procesarPagoPayPal(String emailPayPal) {
        // Simulamos validación
        if (emailPayPal == null || !emailPayPal.contains("@")) return false;

        // Simulamos conexión con API de PayPal...
        // Simulamos que tarda un poco...
        try { Thread.sleep(500); } catch (InterruptedException e) {

            // Restauramos el estado de interrupción para que el hilo sepa que debe pararse.
            Thread.currentThread().interrupt();
            // Si el proceso se interrumpió, el pago ha fallado.
            return false;
        }

        return true; // Pago aceptado
    }
}