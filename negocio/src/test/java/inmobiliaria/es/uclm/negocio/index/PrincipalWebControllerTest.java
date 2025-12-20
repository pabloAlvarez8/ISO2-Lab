package inmobiliaria.es.uclm.negocio.index;

import inmobiliaria.es.uclm.negocio.alojamiento.AlojamientoService_Interfaz;
import inmobiliaria.es.uclm.negocio.alojamiento.dto.DestinoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// Si usas Spring Boot 3.4+ usa @MockitoBean. Si es anterior usa @MockBean
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PrincipalWebController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva la seguridad (Login) para el test
public class PrincipalWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlojamientoService_Interfaz alojamientoService;

    @Test
    void testPaginaDeInicio_DeberiaRetornarVistaIndexYModelo() throws Exception {
        // GIVEN
        // CORRECCIÓN: Al ser un record, debemos pasar los argumentos (Ciudad, Foto)
        DestinoDTO destino1 = new DestinoDTO("Madrid", "http://foto-fake.com/madrid.jpg");
        List<DestinoDTO> listaSimulada = Arrays.asList(destino1);

        when(alojamientoService.obtenerDestinosPopulares()).thenReturn(listaSimulada);

        // WHEN & THEN
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("destinosPopulares"))
                .andExpect(model().attribute("destinosPopulares", listaSimulada));
    }

    @Test
    void testBuscador_DeberiaRetornarVistaBuscador() throws Exception {
        mockMvc.perform(get("/buscador"))
                .andExpect(status().isOk())
                .andExpect(view().name("Buscador"));
    }

    @Test
    void testPago_DeberiaRetornarVistaPago() throws Exception {
        // GIVEN (Simulamos datos de sesión para que Thymeleaf no falle)
        Map<String, Object> alojamientoFake = new HashMap<>();
        alojamientoFake.put("nombre", "Hotel Test");
        alojamientoFake.put("precio", 100.0);

        Map<String, Object> reservaFake = new HashMap<>();
        reservaFake.put("alojamiento", alojamientoFake);

        // WHEN & THEN
        mockMvc.perform(get("/pago")
                        .sessionAttr("reserva", reservaFake)) // Inyectamos 'reserva' en sesión
                .andExpect(status().isOk())
                .andExpect(view().name("pago"));
    }

    @Test
    void testDetalleAlojamientos_DeberiaRetornarVistaDetalle() throws Exception {
        // GIVEN (Simulamos datos de sesión para que Thymeleaf no falle)
        Map<String, Object> alojamientoFake = new HashMap<>();
        alojamientoFake.put("fotoUrl", "https://via.placeholder.com/150");
        alojamientoFake.put("nombre", "Casa Test");
        alojamientoFake.put("descripcion", "Descripción de prueba");
        alojamientoFake.put("precio", 50.0);

        // WHEN & THEN
        mockMvc.perform(get("/detalleAlojamientos.html")
                        .sessionAttr("alojamiento", alojamientoFake)) // Inyectamos 'alojamiento' en sesión
                .andExpect(status().isOk())
                .andExpect(view().name("detalleAlojamientos"));
    }

    @Test
    void testDashboard_ConUrlPrevia_DeberiaRedirigirAUrlPrevia() throws Exception {
        String urlGuardada = "/alojamiento/123";

        mockMvc.perform(get("/dashboard")
                        .sessionAttr("urlPrevio", urlGuardada))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(urlGuardada));
    }

    @Test
    void testDashboard_SinUrlPrevia_DeberiaRedirigirAIndex() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}