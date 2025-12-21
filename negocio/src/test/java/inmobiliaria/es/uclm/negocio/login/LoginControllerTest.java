package inmobiliaria.es.uclm.negocio.login;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldSaveReferrerInSession_WhenComingFromValidPage() throws Exception {
        // Simulamos que el usuario viene de la lista de propiedades
        String validReferrer = "http://localhost:8080/propiedades";

        MvcResult result = mockMvc.perform(get("/login")
                        .header("Referer", validReferrer))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andReturn();

        // Extraemos el valor guardado en la sesión para verificarlo
        String urlEnSesion = (String) result.getRequest().getSession().getAttribute("urlPrevio");

        // El test pasa si el valor en sesión coincide con el Referer
        assertEquals(validReferrer, urlEnSesion);
    }

    @Test
    void shouldNotSaveReferrer_WhenComingFromLoginOrRegister() throws Exception {
        // Simulamos que el usuario ya estaba en login (por ejemplo, falló la clave y recargó)
        String loginReferrer = "http://localhost:8080/login";

        MvcResult result = mockMvc.perform(get("/login")
                        .header("Referer", loginReferrer))
                .andExpect(status().isOk())
                .andReturn();

        // Verificamos que NO se guardó en sesión para evitar bucles infinitos de redirección
        String urlEnSesion = (String) result.getRequest().getSession().getAttribute("urlPrevio");
        assertNull(urlEnSesion);
    }

    @Test
    void shouldNotFail_WhenReferrerIsNull() throws Exception {
        // Caso donde el usuario escribe la URL directamente en el navegador
        MvcResult result = mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andReturn();

        String urlEnSesion = (String) result.getRequest().getSession().getAttribute("urlPrevio");
        assertNull(urlEnSesion);
    }
}