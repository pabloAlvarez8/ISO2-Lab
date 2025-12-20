package inmobiliaria.es.uclm.negocio.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// (1) Cargamos el controlador exacto
@WebMvcTest(UserController.class)
public class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("Should register a user via API and return the created object")
    @WithMockUser // Saltamos la seguridad básica
    public void testRegisterUserApi() throws Exception {
        // 1. Given
        User usuarioGuardado = new User();
        usuarioGuardado.setId(10L);
        usuarioGuardado.setEmail("apiuser@test.com");
        usuarioGuardado.setNombre("API Tester");

        // Mock del servicio
        when(userService.registerUser(any(User.class))).thenReturn(usuarioGuardado);

        // 2. When & Then
        mockMvc.perform(post("/api/users/register") // (2) La ruta debe coincidir con el Controller
                        .param("email", "apiuser@test.com")
                        .param("nombre", "API Tester")
                        .param("password", "secret123")
                        .with(csrf())) // (3) Token de seguridad obligatorio para POST
                .andExpect(status().isOk()) // Esperamos 200 OK
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.email").value("apiuser@test.com"));

        verify(userService).registerUser(any(User.class));
    }
}