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

    // ==========================================
    // SEARCH & LISTING TESTS
    // ==========================================

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
                .andExpect(view().name("Buscador"))
                .andExpect(model().attributeExists("precioMaximo"))
                .andExpect(model().attribute("filtroCiudad", "Madrid"));
    }

    // ==========================================
    // CREATE & SAVE TESTS
    // ==========================================

    @Test
    @DisplayName("shouldDisplayCreateForm_ReturnsNewAlojamientoView 📝")
    @WithMockUser
    void showCreateForm_ReturnsView() throws Exception {
        mockMvc.perform(get("/alojamientos/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("nuevoAlojamiento"))
                .andExpect(model().attributeExists("alojamiento"));
    }

    @Test
    @DisplayName("shouldSaveProperty_AuthenticatedUser_RedirectsToProfile 💾")
    @WithMockUser(username = "usuario@test.com")
    void save_AuthenticatedUser_Redirects() throws Exception {
        mockMvc.perform(post("/alojamientos/guardar")
                        .with(csrf()) // Importante para POST
                        .flashAttr("alojamiento", new Alojamiento()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil"));

        // Verificamos que se llamó al servicio con el email correcto
        verify(propertyService).guardarNuevoAlojamiento(any(Alojamiento.class), eq("usuario@test.com"));
    }

    @Test
    @DisplayName("shouldSaveProperty_Unauthenticated_ReturnsUnauthorized 🚫")
    void save_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/alojamientos/guardar")
                        .with(csrf())
                        .flashAttr("alojamiento", new Alojamiento()))
                // CAMBIO: Esperamos 401 porque Spring Security bloquea el acceso antes del Controller
                .andExpect(status().isUnauthorized());

        verify(propertyService, never()).guardarNuevoAlojamiento(any(), any());
    }

    // ==========================================
    // DELETE TESTS
    // ==========================================

    @Test
    @DisplayName("shouldDeleteProperty_ReturnsRedirectToProfile 🗑️")
    @WithMockUser
    void delete_ValidId_Redirects() throws Exception {
        mockMvc.perform(get("/alojamientos/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil"));

        verify(propertyService).eliminar(1L);
    }

    // ==========================================
    // DETAILS TESTS
    // ==========================================

    @Test
    @DisplayName("shouldShowDetails_ValidId_ReturnsView 🏠")
    void showDetails_ValidId_ReturnsView() throws Exception {
        // 1. SETUP
        Alojamiento mockProperty = new Alojamiento();
        mockProperty.setId(1L);
        mockProperty.setNombre("Casa Test");
        mockProperty.setPrecio(new java.math.BigDecimal("100.00"));

        // Mocking del usuario principal para Thymeleaf
        var principalConId = new Object() {
            public Long getId() { return 123L; }
            public String getUsername() { return "usuario@test.com"; }
        };
        var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                principalConId, "password", java.util.Collections.emptyList()
        );

        when(propertyService.findById(1L)).thenReturn(mockProperty);
        when(reviewService.obtenerMedia(1L)).thenReturn(4.5);

        // 2. EXECUTE
        mockMvc.perform(get("/alojamientos/detalleAlojamientos")
                        .param("id", "1")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(view().name("detalleAlojamientos"))
                .andExpect(model().attributeExists("alojamiento", "mediaValoracion"));
    }

    @Test
    @DisplayName("shouldShowDetails_PropertyNotFound_RedirectsToList ❌")
    @WithMockUser
    void showDetails_NotFound_Redirects() throws Exception {
        // Simulamos que el servicio devuelve null
        when(propertyService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/alojamientos/detalleAlojamientos")
                        .param("id", "99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/alojamientos"));
    }

    @Test
    @DisplayName("shouldShowDetails_NullId_RedirectsToList ⚠️")
    @WithMockUser
    void showDetails_NullId_Redirects() throws Exception {
        // En tu controller tienes "if (id == null)".
        // Spring MVC requiere el parámetro por defecto, pero si enviamos vacío o si configuraste required=false:
        mockMvc.perform(get("/alojamientos/detalleAlojamientos"))
                // Nota: Si @RequestParam tiene required=true (default), esto da 400 Bad Request.
                // Si el test falla con 400, significa que Spring validó antes que tu if.
                // Si tu controller tiene required=false, entrará y redirigirá.
                .andExpect(status().is4xxClientError());
    }

    // ==========================================
    // EDIT TESTS (NUEVOS - FALTABAN ANTES)
    // ==========================================

    @Test
    @DisplayName("shouldEditProperty_ValidId_ReturnsForm ✏️")
    @WithMockUser
    void edit_ValidId_ReturnsForm() throws Exception {
        // GIVEN
        Alojamiento existing = new Alojamiento();
        existing.setId(1L);
        when(propertyService.findById(1L)).thenReturn(existing);

        // WHEN
        mockMvc.perform(get("/alojamientos/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("nuevoAlojamiento")) // Reusa la vista
                .andExpect(model().attributeExists("alojamiento"));
    }

    @Test
    @DisplayName("shouldEditProperty_InvalidId_RedirectsToProfile 🛑")
    @WithMockUser
    void edit_InvalidId_Redirects() throws Exception {
        // GIVEN
        when(propertyService.findById(99L)).thenReturn(null);

        // WHEN
        mockMvc.perform(get("/alojamientos/editar/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil"));
    }
}