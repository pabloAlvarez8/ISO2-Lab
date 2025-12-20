package inmobiliaria.es.uclm.negocio.reserva;

import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import inmobiliaria.es.uclm.negocio.alojamiento.AlojamientoService_Interfaz;
import inmobiliaria.es.uclm.negocio.user.User;
import inmobiliaria.es.uclm.negocio.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservaService reservaService;

    @MockitoBean
    private AlojamientoService_Interfaz alojamientoService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ReservaRepository reservaRepository;

    @Test
    @WithMockUser(username = "inquilino@uclm.es")
    void shouldCreateReservationAndRedirectToPayment() throws Exception {
        // 1. Given
        User inquilino = new User();
        inquilino.setEmail("inquilino@uclm.es");

        Alojamiento alojamiento = new Alojamiento();
        alojamiento.setId(1L);
        alojamiento.setPrecio(BigDecimal.valueOf(100.0));

        // CORREGIDO: Usamos fechas de 2026 para asegurar que sean "Futuras"
        String entrada = "2026-01-01";
        String salida = "2026-01-05";

        Reserva reservaGuardada = new Reserva();
        reservaGuardada.setId(55L);
        reservaGuardada.setEstado("PENDIENTE_PAGO");

        // 2. Mocks
        when(userService.findByEmail("inquilino@uclm.es")).thenReturn(Optional.of(inquilino));
        when(alojamientoService.findById(1L)).thenReturn(alojamiento);
        // Repository devuelve 0L (Long)
        when(reservaRepository.countSolapamientos(anyLong(), any(), any())).thenReturn(0L);
        when(reservaService.guardar(any(Reserva.class))).thenReturn(reservaGuardada);

        // 3. Perform
        mockMvc.perform(post("/reservas/crear")
                        .param("alojamientoId", "1")
                        .param("fechaEntrada", entrada)
                        .param("fechaSalida", salida)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pagos/pago/55"));

        verify(reservaService).guardar(any(Reserva.class));
    }

    @Test
    @WithMockUser(username = "inquilino@uclm.es")
    void shouldRedirectToDetail_WhenDatesOverlap() throws Exception {
        User inquilino = new User();
        inquilino.setEmail("inquilino@uclm.es");

        // Fechas futuras
        String entrada = "2026-02-01";
        String salida = "2026-02-05";

        when(userService.findByEmail("inquilino@uclm.es")).thenReturn(Optional.of(inquilino));
        when(alojamientoService.findById(1L)).thenReturn(new Alojamiento());

        // Simulamos solapamiento (1L)
        when(reservaRepository.countSolapamientos(anyLong(), any(), any())).thenReturn(1L);

        mockMvc.perform(post("/reservas/crear")
                        .param("alojamientoId", "1")
                        .param("fechaEntrada", entrada)
                        .param("fechaSalida", salida)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/alojamientos/detalle/1"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "inquilino@uclm.es")
    void shouldShowBookingDetail_WhenUserIsTenant() throws Exception {
        User inquilino = new User();
        inquilino.setEmail("inquilino@uclm.es");
        User anfitrion = new User();
        anfitrion.setEmail("host@uclm.es");

        Alojamiento alojamiento = new Alojamiento();
        alojamiento.setAnfitrion(anfitrion);

        Reserva reserva = new Reserva();
        reserva.setId(10L);
        reserva.setInquilino(inquilino);
        reserva.setAlojamiento(alojamiento);
        reserva.setFechaEntrada(LocalDate.now());
        reserva.setFechaSalida(LocalDate.now().plusDays(2));
        // ESTADO STRING (para que coincida con tu HTML arreglado)
        reserva.setEstado("CONFIRMADA");

        when(reservaRepository.findById(10L)).thenReturn(Optional.of(reserva));

        mockMvc.perform(get("/reservas/booking-detail/10"))
                .andExpect(status().isOk())
                .andExpect(view().name("booking-details"))
                .andExpect(model().attributeExists("reserva"));
    }

    @Test
    @WithMockUser(username = "hacker@uclm.es")
    void shouldRedirectError_WhenUserIsNotAuthorizedForDetail() throws Exception {
        User inquilino = new User();
        inquilino.setEmail("inquilino@uclm.es");
        User anfitrion = new User();
        anfitrion.setEmail("host@uclm.es");

        Alojamiento alojamiento = new Alojamiento();
        alojamiento.setAnfitrion(anfitrion);

        Reserva reserva = new Reserva();
        reserva.setInquilino(inquilino);
        reserva.setAlojamiento(alojamiento);
        reserva.setEstado("CONFIRMADA");

        when(reservaRepository.findById(10L)).thenReturn(Optional.of(reserva));

        mockMvc.perform(get("/reservas/booking-detail/10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil?error=NoTienesPermiso"));
    }

    @Test
    @WithMockUser
    void shouldAcceptReservation() throws Exception {
        Reserva reserva = new Reserva();
        reserva.setId(1L);
        when(reservaService.findById(1L)).thenReturn(reserva);

        mockMvc.perform(get("/reservas/aceptar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil"));

        verify(reservaService).guardar(reserva);
    }

    @Test
    @WithMockUser
    void shouldReturnOccupiedDatesApi() throws Exception {
        Reserva reserva = new Reserva();
        reserva.setFechaEntrada(LocalDate.of(2026, 1, 1));
        reserva.setFechaSalida(LocalDate.of(2026, 1, 5));

        when(reservaRepository.findReservasFuturas(1L)).thenReturn(Collections.singletonList(reserva));

        mockMvc.perform(get("/reservas/api/ocupadas/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("2026-01-01:2026-01-05")));
    }
}