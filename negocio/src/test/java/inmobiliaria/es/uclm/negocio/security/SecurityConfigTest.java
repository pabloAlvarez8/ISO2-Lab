package inmobiliaria.es.uclm.negocio.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ----------------------------------------------------------------
    // 1. Test de Rutas PÚBLICAS (permitAll)
    // ----------------------------------------------------------------
    @Test
    void accesoPublico_DeberiaPermitirAccesoSinLogin() throws Exception {
        // Probamos la raíz "/" que definiste como pública
        // Nota: Si tu controlador redirige (como vimos en otros tests), esperamos 3xx o 2xx, 
        // pero LO IMPORTANTE es que NO redirija al login.
        mockMvc.perform(get("/"))
                .andExpect(status().isOk()) // O is3xxRedirection() si "/" redirige
                .andExpect(view().name("index")); // Asumiendo que "/" va a index
    }

    @Test
    void accesoLogin_DeberiaSerPublico() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void accesoRegistro_DeberiaSerPublico() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    // ----------------------------------------------------------------
    // 2. Test de Rutas PRIVADAS sin Autenticación
    // ----------------------------------------------------------------
    @Test
    void accesoPrivado_SinLogin_DeberiaRedirigirALogin() throws Exception {
        // Intentamos entrar al dashboard sin estar logueados
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                // Spring Security redirige a http://localhost/login por defecto
                .andExpect(redirectedUrlPattern("**/login"));
    }

    // ----------------------------------------------------------------
    // 3. Test de Rutas PRIVADAS CON Usuario (Simulado)
    // ----------------------------------------------------------------
    @Test
    @WithMockUser(username = "usuario", roles = "USER") // <--- Simula estar logueado
    void accesoPrivado_ConLogin_DeberiaPermitirAcceso() throws Exception {
        /*
         * NOTA IMPORTANTE:
         * En tu PrincipalWebController, "/dashboard" redirige a "/" si no hay historial.
         * Por lo tanto, aquí esperamos un 302 (Redirección a /), NO un 200 OK.
         * Lo que probamos es que Security NO nos bloqueó ni nos mandó al login.
         */
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/")); // El controlador manda aquí, no Security
    }

    // ----------------------------------------------------------------
    // 4. Test del Bean PasswordEncoder
    // ----------------------------------------------------------------
    @Test
    void passwordEncoder_DeberiaEncriptarYVerificar() {
        String passwordPlano = "secreto123";

        // 1. Encriptar
        String hash = passwordEncoder.encode(passwordPlano);
        
        // Verificaciones
        assertNotEquals(passwordPlano, hash, "La contraseña no debe guardarse en texto plano");
        assertTrue(passwordEncoder.matches(passwordPlano, hash), "El encoder debe validar el password correcto");
        System.out.println("Hash generado: " + hash); // Opcional: para verlo en consola
    }
}