package inmobiliaria.es.uclm.negocio.pago;

import inmobiliaria.es.uclm.negocio.reserva.Reserva;
import inmobiliaria.es.uclm.negocio.reserva.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagoController.class)
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservaService reservaService;

    @MockitoBean
    private PagoService pagoService;

    // Objeto dummy para usar en los tests
    private Reserva reservaMock;

    @BeforeEach
    void setUp() {
        // Inicializamos una reserva básica antes de cada test
        reservaMock = new Reserva();
        reservaMock.setId(1L);
        reservaMock.setPrecioTotal(100.0);
        reservaMock.setEstado("PENDIENTE");
    }

    // --------------------------------------------------------
    // 1. Tests para MOSTRAR PANTALLA (GET)
    // --------------------------------------------------------

    @Test
    void mostrarPasarela_ReservaExistente_DeberiaMostrarVistaPago() throws Exception {
        // GIVEN
        when(reservaService.findById(1L)).thenReturn(reservaMock);

        // WHEN & THEN
        mockMvc.perform(get("/pagos/pago/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("pago"))
                .andExpect(model().attribute("reserva", reservaMock))
                .andExpect(model().attribute("precioAPagar", 100.0));
    }

    @Test
    void mostrarPasarela_ReservaNoExiste_DeberiaLanzarExcepcion() throws Exception {
        // GIVEN
        when(reservaService.findById(99L)).thenReturn(null);

        // WHEN & THEN
        // JUnit espera que el controlador lance la excepción RuntimeException
        mockMvc.perform(get("/pagos/pago/99"))
                .andExpect(status().isInternalServerError()) // O la excepción que maneje tu GlobalExceptionHandler
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RuntimeException));
    }

    // --------------------------------------------------------
    // 2. Tests para PROCESAR PAGO (POST)
    // --------------------------------------------------------

    @Test
    void procesarPago_TarjetaExito_DeberiaRedirigirAPerfilYGuardar() throws Exception {
        // GIVEN
        when(reservaService.findById(1L)).thenReturn(reservaMock);
        // Simulamos que el servicio de pago dice "TRUE"
        when(pagoService.procesarPagoTarjeta(anyString(), anyString(), anyString())).thenReturn(true);

        // WHEN
        mockMvc.perform(post("/pagos/procesar")
                        .param("idReserva", "1")
                        .param("metodoPago", "tarjeta")
                        .param("numeroTarjeta", "1234567890123456") // simulado
                        .param("emailPaypal", "")) 
                // THEN
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil"));

        // Verificamos que se cambió el estado y se guardó
        assertEquals("PAGADO", reservaMock.getEstado());
        verify(reservaService).guardar(reservaMock);
    }

    @Test
    void procesarPago_PaypalExito_DeberiaRedirigirAPerfil() throws Exception {
        // GIVEN
        when(reservaService.findById(1L)).thenReturn(reservaMock);
        // Simulamos que el servicio de pago PayPal dice "TRUE"
        when(pagoService.procesarPagoPayPal(anyString())).thenReturn(true);

        // WHEN
        mockMvc.perform(post("/pagos/procesar")
                        .param("idReserva", "1")
                        .param("metodoPago", "paypal")
                        .param("emailPaypal", "test@test.com"))
                // THEN
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil"));

        verify(reservaService).guardar(reservaMock);
    }

    @Test
    void procesarPago_Fallo_DeberiaRedirigirConError() throws Exception {
        // GIVEN
        when(reservaService.findById(1L)).thenReturn(reservaMock);
        // Simulamos que el servicio de pago dice "FALSE" (falló)
        when(pagoService.procesarPagoTarjeta(anyString(), anyString(), anyString())).thenReturn(false);

        // WHEN
        mockMvc.perform(post("/pagos/procesar")
                        .param("idReserva", "1")
                        .param("metodoPago", "tarjeta")
                        .param("numeroTarjeta", "000")) // tarjeta mala
                // THEN
                .andExpect(status().is3xxRedirection())
                // Esperamos que vuelva a la página de pago con ?error=true
                .andExpect(redirectedUrl("/pagos/pago/1?error=true"));

        // Verificamos que NO se guardó como pagado
        verify(reservaService, never()).guardar(any(Reserva.class));
    }
}