package inmobiliaria.es.uclm.negocio.perfil;

import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import inmobiliaria.es.uclm.negocio.alojamiento.AlojamientoService;
import inmobiliaria.es.uclm.negocio.reserva.Reserva;
import inmobiliaria.es.uclm.negocio.reserva.ReservaRepository;
import inmobiliaria.es.uclm.negocio.user.User;
import inmobiliaria.es.uclm.negocio.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PerfilWebController.class)
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean(name = "userService")
    private UserService userService;

    @MockitoBean
    private AlojamientoService alojamientoService;

    @MockitoBean
    private ReservaRepository reservaRepository;

    @Test
    @DisplayName("Should display user profile with all lists loaded (Hosts & Tenant data)")
    @WithMockUser(username = "usuario@uclm.es")
    public void shouldDisplayUserProfile_WhenUserIsAuthenticated() throws Exception {
        // 1. Given
        User usuario = new User();
        usuario.setId(1L);
        usuario.setEmail("usuario@uclm.es");
        usuario.setNombre("Pepe");

        // --- DATA SETUP TO PREVENT THYMELEAF ERRORS ---
        Alojamiento alojamientoFalso = new Alojamiento();
        alojamientoFalso.setId(10L);
        alojamientoFalso.setNombre("Test House"); // Required by HTML

        Reserva reservaFalsa = new Reserva();
        reservaFalsa.setId(50L);
        reservaFalsa.setAlojamiento(alojamientoFalso); // Avoids NullPointerException in view
        reservaFalsa.setEstado("PENDIENTE_PAGO");

        List<Alojamiento> misAlojamientos = Collections.singletonList(alojamientoFalso);
        List<Reserva> solicitudes = Collections.emptyList();
        List<Reserva> misReservas = Collections.singletonList(reservaFalsa);

        // 2. Mocks
        // For Controller logic
        when(userService.findByEmail("usuario@uclm.es")).thenReturn(Optional.of(usuario));
        // For Thymeleaf Header logic
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(usuario));

        when(alojamientoService.listarAlojamientosDeAnfitrion(1L)).thenReturn(misAlojamientos);
        when(reservaRepository.findByAlojamiento_Anfitrion_Id(1L)).thenReturn(solicitudes);
        when(reservaRepository.findByInquilino_Id(1L)).thenReturn(misReservas);

        // 3. Perform & Verify
        mockMvc.perform(get("/perfil"))
                .andExpect(status().isOk())
                .andExpect(view().name("perfil"))
                .andExpect(model().attribute("usuario", usuario))
                .andExpect(model().attribute("misAlojamientos", misAlojamientos))
                .andExpect(model().attribute("solicitudesRecibidas", solicitudes))
                .andExpect(model().attribute("misReservas", misReservas));
    }

    @Test
    @DisplayName("Should return 401 (Unauthorized) if user is not authenticated")
    public void shouldReturnUnauthorized_WhenUserIsNotLoggedIn() throws Exception {
        mockMvc.perform(get("/perfil"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should show the 'Become a Host' landing page")
    @WithMockUser(username = "user")
    public void shouldShowBecomeHostPage() throws Exception {
        // Mock for header
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        mockMvc.perform(get("/anfitrion"))
                .andExpect(status().isOk())
                .andExpect(view().name("anfitrion"));
    }

    @Test
    @DisplayName("Should process 'Become a Host' form and redirect to profile")
    @WithMockUser(username = "futuroanfitrion@uclm.es")
    public void shouldProcessBecomeHostForm_AndRedirectToProfile() throws Exception {
        // 1. Given
        User usuario = new User();
        usuario.setId(5L);
        usuario.setEmail("futuroanfitrion@uclm.es");

        when(userService.findByEmail("futuroanfitrion@uclm.es")).thenReturn(Optional.of(usuario));
        // Generic mock for header
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(usuario));

        // 2. Perform POST
        mockMvc.perform(post("/perfil/convertirse-anfitrion")
                        .param("dni", "12345678Z")
                        .param("telefono", "666777888")
                        .param("iban", "ES1234567890")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil"));

        // 3. Verify
        verify(userService).convertirEnAnfitrion(5L, "12345678Z", "666777888", "ES1234567890");
    }
}