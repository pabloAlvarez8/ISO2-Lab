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
import static org.mockito.ArgumentMatchers.eq;
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
    private AlojamientoService_Interfaz propertyService;

    // FIX: Forzamos el nombre "userService" para que Thymeleaf (@userService) lo encuentre
    @MockitoBean(name = "userService")
    private UserService userService;

    @MockitoBean
    private ValoracionService reviewService;

    @Test
    @DisplayName("shouldDisplaySearchPage_ReturnsBuscadorView_WithAttributes 🔍")
    @WithMockUser
    void showSearchPage_ReturnsBuscadorView() throws Exception {
        // 1. GIVEN
        List<String> mockTypes = Arrays.asList("Casa", "Piso");
        when(propertyService.obtenerPrecioMaximoAlojamientoRedondeado()).thenReturn(1000L);
        when(propertyService.obtenerTodosLosTipos()).thenReturn(mockTypes);

        // 2. WHEN & 3. THEN
        mockMvc.perform(get("/alojamientos")
                        .param("q", "Madrid")
                        .param("type", "Casa"))
                .andExpect(status().isOk())
                .andExpect(view().name("Buscador"))
                .andExpect(model().attribute("precioMaximo", 1000L))
                .andExpect(model().attribute("listaTipos", mockTypes));
    }

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
        // 1. WHEN
        mockMvc.perform(post("/alojamientos/guardar")
                        .with(csrf()) // Token de seguridad obligatorio en POST
                        .flashAttr("alojamiento", new Alojamiento()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil"));

        // 2. THEN (Verificamos que el controlador llamó al servicio)
        verify(propertyService).guardarNuevoAlojamiento(any(Alojamiento.class), eq("usuario@test.com"));
    }

    @Test
    @DisplayName("shouldDeleteProperty_ReturnsRedirectToProfile 🗑️")
    @WithMockUser
    void delete_ValidId_Redirects() throws Exception {
        // 1. WHEN
        mockMvc.perform(get("/alojamientos/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil"));

        // 2. THEN
        verify(propertyService).eliminar(1L);
    }

    @Test
    @DisplayName("shouldShowDetails_ValidId_ReturnsDetailView 🏠")
    @WithMockUser
    void showDetails_ValidId_ReturnsView() throws Exception {
        // 1. GIVEN
        Alojamiento mockProperty = new Alojamiento();
        mockProperty.setId(1L);
        when(propertyService.findById(1L)).thenReturn(mockProperty);
        when(reviewService.obtenerMedia(1L)).thenReturn(4.5);

        // 2. WHEN
        mockMvc.perform(get("/alojamientos/detalleAlojamientos").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("detalleAlojamientos"))
                .andExpect(model().attribute("mediaValoracion", 4.5));
    }
}