package inmobiliaria.es.uclm.negocio.pago;

import inmobiliaria.es.uclm.negocio.reserva.Reserva;
import inmobiliaria.es.uclm.negocio.reserva.ReservaService;
import inmobiliaria.es.uclm.negocio.user.UserService;
import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento; 

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagoController.class)
@WithMockUser(username = "usuario", roles = {"USER"})
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservaService reservaService;

    @MockitoBean
    private PagoService pagoService;

    @MockitoBean(name = "userService")
    private UserService userService;

    private Reserva reservaMock;

    @BeforeEach
    void setUp() {
        reservaMock = new Reserva();
        reservaMock.setId(1L);
        reservaMock.setPrecioTotal(100.0);
        reservaMock.setEstado("PENDIENTE");

        // --- CORRECCIÓN ---
        // Inicializamos el Alojamiento para evitar que Thymeleaf falle 
        // al acceder a "reserva.alojamiento.nombre"
        Alojamiento alojamiento = new Alojamiento();
        alojamiento.setNombre("Casa de Prueba");
        reservaMock.setAlojamiento(alojamiento);
    }

    // 1. Tests para MOSTRAR PANTALLA (GET)
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
    void mostrarPasarela_ReservaNoExiste_DeberiaLanzarExcepcion() {
        // GIVEN
        when(reservaService.findById(99L)).thenReturn(null);

        // WHEN & THEN
        ServletException exception = assertThrows(ServletException.class, () -> {
            mockMvc.perform(get("/pagos/pago/99"));
        });

        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Reserva no encontrada", exception.getCause().getMessage());
    }

    // 2. Tests para PROCESAR PAGO (POST)
    @Test
    void procesarPago_TarjetaExito_DeberiaRedirigirAPerfilYGuardar() throws Exception {
        // GIVEN
        when(reservaService.findById(1L)).thenReturn(reservaMock);
        when(pagoService.procesarPagoTarjeta(anyString(), anyString(), anyString())).thenReturn(true);

        // WHEN
        mockMvc.perform(post("/pagos/procesar")
                        .with(csrf())
                        .param("idReserva", "1")
                        .param("metodoPago", "tarjeta")
                        .param("numeroTarjeta", "1234567890123456")
                        .param("emailPaypal", "")) 
                // THEN
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil"));

        assertEquals("PAGADO", reservaMock.getEstado());
        verify(reservaService).guardar(reservaMock);
    }

    @Test
    void procesarPago_PaypalExito_DeberiaRedirigirAPerfil() throws Exception {
        // GIVEN
        when(reservaService.findById(1L)).thenReturn(reservaMock);
        when(pagoService.procesarPagoPayPal(anyString())).thenReturn(true);

        // WHEN
        mockMvc.perform(post("/pagos/procesar")
                        .with(csrf())
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
        when(pagoService.procesarPagoTarjeta(anyString(), anyString(), anyString())).thenReturn(false);

        // WHEN
        mockMvc.perform(post("/pagos/procesar")
                        .with(csrf())
                        .param("idReserva", "1")
                        .param("metodoPago", "tarjeta")
                        .param("numeroTarjeta", "000")) 
                // THEN
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pagos/pago/1?error=true"));

        verify(reservaService, never()).guardar(any(Reserva.class));
    }
}