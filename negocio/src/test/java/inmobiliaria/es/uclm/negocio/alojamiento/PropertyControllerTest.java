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
import org.springframework.ui.Model; // Importante para el mock

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals; // Para assert de Strings
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

    // INYECCIÓN DIRECTA: Nos permite llamar a los métodos saltando validaciones HTTP
    @Autowired
    private AlojamientoController controller;

    @MockitoBean
    private AlojamientoService propertyService;

    @MockitoBean(name = "userService")
    private UserService userService;

    @MockitoBean
    private ValoracionService reviewService;

    // ==========================================
    // TESTS WEB (MOCK MVC) - Comportamiento HTTP
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
        Alojamiento mockProperty = new Alojamiento();
        mockProperty.setId(1L);
        mockProperty.setNombre("Casa Test");
        mockProperty.setPrecio(new java.math.BigDecimal("100.00"));

        inmobiliaria.es.uclm.negocio.user.User principalUser = new inmobiliaria.es.uclm.negocio.user.User();
        principalUser.setId(123L);
        principalUser.setEmail("usuario@test.com");

        var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                principalUser, "password", java.util.Collections.emptyList()
        );

        when(propertyService.findById(1L)).thenReturn(mockProperty);
        when(reviewService.obtenerMedia(1L)).thenReturn(4.5);

        mockMvc.perform(get("/alojamientos/detalleAlojamientos")
                        .param("id", "1")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(view().name("detalleAlojamientos"));
    }

    @Test
    @DisplayName("edit_ValidId_ReturnsForm ✏️")
    @WithMockUser
    void edit_ValidId_ReturnsForm() throws Exception {
        Alojamiento existing = new Alojamiento();
        existing.setId(1L);
        when(propertyService.findById(1L)).thenReturn(existing);

        mockMvc.perform(get("/alojamientos/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("nuevoAlojamiento"));
    }

    @Test
    @DisplayName("edit_InvalidId_Redirects 🛑")
    @WithMockUser
    void edit_InvalidId_Redirects() throws Exception {
        when(propertyService.findById(99L)).thenReturn(null);
        mockMvc.perform(get("/alojamientos/editar/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil"));
    }

    // =================================================================
    // TESTS UNITARIOS DIRECTOS (Bypassing Spring MVC Validation/Security)
    // Para cubrir código defensivo inalcanzable vía HTTP normal
    // =================================================================

    @Test
    @DisplayName("DIRECT CALL: showDetails with NULL ID redirects 🛡️")
    void unitTest_showDetails_NullId_Redirects() {
        // Llamamos directamente al método Java, saltándonos el chequeo @RequestParam de Spring
        Model mockModel = mock(Model.class);

        String viewName = controller.detalleAlojamientos(null, mockModel);

        assertEquals("redirect:/alojamientos", viewName);
    }

    @Test
    @DisplayName("DIRECT CALL: save with NULL Authentication redirects to login 🛡️")
    void unitTest_save_NullAuth_RedirectsLogin() {
        // Llamamos directamente al método Java, saltándonos los filtros de Spring Security
        Alojamiento dummy = new Alojamiento();

        String viewName = controller.guardar(dummy, null);

        assertEquals("redirect:/login", viewName);
    }

    @Test
    @DisplayName("DIRECT CALL: showDetails when Property Not Found redirects 👻")
    void unitTest_showDetails_NotFound_Redirects() {
        // 1. GIVEN: Un ID que no existe en BD
        Long idNoExistente = 99L;
        Model mockModel = mock(Model.class);

        // Simulamos que el servicio NO encuentra nada (devuelve null)
        when(propertyService.findById(idNoExistente)).thenReturn(null);

        // 2. WHEN: Llamamos al controlador directamente
        String viewName = controller.detalleAlojamientos(idNoExistente, mockModel);

        // 3. THEN: Debe redirigir al listado
        assertEquals("redirect:/alojamientos", viewName);
    }
}