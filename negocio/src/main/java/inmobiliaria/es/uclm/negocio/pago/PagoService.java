package inmobiliaria.es.uclm.negocio.pago;

import org.springframework.stereotype.Service;


@Service
public class PagoService {

    // LÓGICA DE TARJETA
    public boolean procesarPagoTarjeta(String numeroTarjeta, String caducidad, String cvv) {
        return (numeroTarjeta != null && numeroTarjeta.length() > 13);
    }

    // LÓGICA DE PAYPAL
    public boolean procesarPagoPayPal(String emailPayPal) {
        // Simulamos validación
        if (emailPayPal == null || !emailPayPal.contains("@")) return false;

        // Simulamos conexión con API de PayPal...
        // Simulamos que tarda un poco...
        try { 
            Thread.sleep(500); 
        } catch (InterruptedException e) {
            // Compliant: Restore the interrupted status so the thread knows it should stop
            Thread.currentThread().interrupt();
        }

        return true; // Pago aceptado
    }
}