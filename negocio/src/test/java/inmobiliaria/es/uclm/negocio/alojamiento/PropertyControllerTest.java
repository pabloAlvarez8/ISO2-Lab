package inmobiliaria.es.uclm.negocio.alojamiento;

import inmobiliaria.es.uclm.negocio.user.UserService;
import inmobiliaria.es.uclm.negocio.valoracion.ValoracionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlojamientoController.class)
class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlojamientoService propertyService;

    @MockitoBean(name = "userService")
    private UserService userService;

    @MockitoBean
    private ValoracionService reviewService;

    @Test
    @DisplayName("shouldDisplaySearchPage_ReturnsBuscadorView_WithAttributes 🔍")
    @WithMockUser
    void showSearchPage_ReturnsBuscadorView() throws Exception {
        List<String> mockTypes = Arrays.asList("Casa", "Piso");
        when(propertyService.obtenerPrecioMaximoAlojamientoRedondeado()).thenReturn(1000L);
        when(propertyService.obtenerTodosLosTipos()).thenReturn(mockTypes);

        mockMvc.perform(get("/alojamientos")
                        .param("q", "Madrid")
                        .param("type", "Casa"))
                .andExpect(status().isOk())
                .andExpect(view().name("Buscador"));
    }

    @Test
    @DisplayName("shouldDisplayCreateForm_ReturnsNewAlojamientoView 📝")
    @WithMockUser
    void showCreateForm_ReturnsView() throws Exception {
        mockMvc.perform(get("/alojamientos/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("nuevoAlojamiento"));
    }

    @Test
    @DisplayName("shouldSaveProperty_AuthenticatedUser_RedirectsToProfile 💾")
    @WithMockUser(username = "usuario@test.com")
    void save_AuthenticatedUser_Redirects() throws Exception {
        mockMvc.perform(post("/alojamientos/guardar")
                        .with(csrf())
                        .flashAttr("alojamiento", new Alojamiento()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil"));
    }

    @Test
    @DisplayName("shouldDeleteProperty_ReturnsRedirectToProfile 🗑️")
    @WithMockUser
    void delete_ValidId_Redirects() throws Exception {
        mockMvc.perform(get("/alojamientos/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil"));
    }

    @Test
    @DisplayName("shouldShowDetails_ValidId_ReturnsView 🏠")
    void showDetails_ValidId_ReturnsView() throws Exception {
        // 1. GIVEN
        Alojamiento mockProperty = new Alojamiento();
        mockProperty.setId(1L);
        mockProperty.setNombre("Casa de Prueba"); // Rellenamos datos para evitar NullPointerException en la vista
        mockProperty.setPrecio(new java.math.BigDecimal("100.00"));

        // Creamos un usuario "dummy" que tenga el método getId() que busca tu HTML
        // Esto engaña a Thymeleaf cuando hace #authentication.principal.id
        var principalConId = new Object() {
            public Long getId() { return 123L; }
            public String getUsername() { return "usuario@test.com"; }
        };

        // Creamos el token de autenticación manual
        var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                principalConId, "password", java.util.Collections.emptyList()
        );

        when(propertyService.findById(1L)).thenReturn(mockProperty);
        when(reviewService.obtenerMedia(1L)).thenReturn(4.5);

        // Mock del servicio por si acaso el HTML lo llama también
        inmobiliaria.es.uclm.negocio.user.User userEntity = new inmobiliaria.es.uclm.negocio.user.User();
        userEntity.setId(123L);
        when(userService.findByEmail(any())).thenReturn(java.util.Optional.of(userEntity));

        // 2. WHEN & 3. THEN
        mockMvc.perform(get("/alojamientos/detalleAlojamientos")
                        .param("id", "1")
                        // AQUÍ ESTÁ LA CLAVE: Inyectamos nuestro principal "trucado"
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(view().name("detalleAlojamientos"));
    }
}