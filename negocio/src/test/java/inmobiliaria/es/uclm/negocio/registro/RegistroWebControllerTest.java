package inmobiliaria.es.uclm.negocio.registro;

import inmobiliaria.es.uclm.negocio.user.User;
import inmobiliaria.es.uclm.negocio.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // Import needed
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// FIX 1: Disable security filters to avoid 401 (Unauthorized) and 403 (CSRF/Forbidden) errors
@WebMvcTest(RegistroWebController.class)
@AutoConfigureMockMvc(addFilters = false) 
class RegistroWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    // ----------------------------------------------------------------
    // 1. Test GET: Mostrar formulario
    // ----------------------------------------------------------------
    @Test
    void showRegisterPage_DeberiaRetornarVistaRegisterYModeloVacio() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    // ----------------------------------------------------------------
    // 2. Test POST: Registro Exitoso
    // ----------------------------------------------------------------
    @Test
    void registerUser_Exito_DeberiaRedirigirYMostrarMensajeExito() throws Exception {
        // FIX 2: 'registerUser' returns a value (User), it is not void.
        // We use 'when(...).thenReturn(...)' instead of 'doNothing()'.
        when(userService.registerUser(any(User.class))).thenReturn(new User());

        mockMvc.perform(post("/register")
                        .param("email", "nuevo@uclm.es")
                        .param("password", "secreto")
                        .param("nombre", "Juan")) 
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register")) 
                .andExpect(flash().attribute("successMessage", "Usuario registrado correctamente."));
        
        verify(userService, times(1)).registerUser(any(User.class));
    }

    // ----------------------------------------------------------------
    // 3. Test POST: Fallo por Error de Negocio (IllegalArgumentException)
    // ----------------------------------------------------------------
    @Test
    void registerUser_EmailDuplicado_DeberiaRedirigirConErrorYDatos() throws Exception {
        // FIX 3: Use 'when(...).thenThrow(...)' for non-void methods
        String errorMsg = "El email ya está en uso";
        when(userService.registerUser(any(User.class))).thenThrow(new IllegalArgumentException(errorMsg));

        mockMvc.perform(post("/register")
                        .param("email", "duplicado@uclm.es")
                        .param("nombre", "Pedro"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attribute("errorMessage", errorMsg))
                .andExpect(flash().attributeExists("user")); 

        verify(userService).registerUser(any(User.class));
    }

    // ----------------------------------------------------------------
    // 4. Test POST: Fallo Inesperado (Exception genérica)
    // ----------------------------------------------------------------
    @Test
    void registerUser_ErrorInesperado_DeberiaMostrarMensajeGenerico() throws Exception {
        // FIX 3: Use 'when(...).thenThrow(...)' for non-void methods
        when(userService.registerUser(any(User.class))).thenThrow(new RuntimeException("Base de datos caída"));

        mockMvc.perform(post("/register")
                        .param("email", "error@uclm.es"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attribute("errorMessage", "Error inesperado durante el registro."))
                .andExpect(flash().attributeExists("user"));
    }
}