package inmobiliaria.es.uclm.negocio.pago;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PagoServiceTest {

    // Instanciamos el servicio directamente (Unit Testing puro)
    private final PagoService pagoService = new PagoService();

    // --------------------------------------------------------
    // Tests para TARJETA
    // --------------------------------------------------------

    @Test
    void procesarPagoTarjeta_NumeroValido_DeberiaDevolverTrue() {
        // GIVEN: Una tarjeta con más de 13 dígitos
        String tarjetaValida = "1234567890123456"; 

        // WHEN
        boolean resultado = pagoService.procesarPagoTarjeta(tarjetaValida, "12/25", "123");

        // THEN
        assertTrue(resultado, "El pago debería ser aceptado con una tarjeta válida");
    }

    @Test
    void procesarPagoTarjeta_NumeroCorto_DeberiaDevolverFalse() {
        // GIVEN: Una tarjeta corta
        String tarjetaCorta = "123"; 

        // WHEN
        boolean resultado = pagoService.procesarPagoTarjeta(tarjetaCorta, "12/25", "123");

        // THEN
        assertFalse(resultado, "El pago debería fallar si la tarjeta es muy corta");
    }

    @Test
    void procesarPagoTarjeta_Null_DeberiaDevolverFalse() {
        assertFalse(pagoService.procesarPagoTarjeta(null, "12/25", "123"));
    }

    // --------------------------------------------------------
    // Tests para PAYPAL
    // --------------------------------------------------------

    @Test
    void procesarPagoPayPal_EmailValido_DeberiaDevolverTrue() {
        String emailValido = "usuario@ejemplo.com";
        assertTrue(pagoService.procesarPagoPayPal(emailValido));
    }

    @Test
    void procesarPagoPayPal_EmailSinArroba_DeberiaDevolverFalse() {
        String emailInvalido = "usuarioejemplo.com";
        assertFalse(pagoService.procesarPagoPayPal(emailInvalido));
    }

    @Test
    void procesarPagoPayPal_Null_DeberiaDevolverFalse() {
        assertFalse(pagoService.procesarPagoPayPal(null));
    }
}