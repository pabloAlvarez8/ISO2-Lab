package inmobiliaria.es.uclm.negocio.index;

import inmobiliaria.es.uclm.negocio.alojamiento.AlojamientoService_Interfaz;
import inmobiliaria.es.uclm.negocio.alojamiento.dto.DestinoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// 1. NUEVO IMPORT para Spring Boot 3.4+
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PrincipalWebController.class)
public class PrincipalWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // 2. CAMBIO: Usamos @MockitoBean en lugar de @MockBean
    @MockitoBean
    private AlojamientoService_Interfaz alojamientoService;

    // --- Los tests permanecen idénticos ---

    @Test
    void testPaginaDeInicio_DeberiaRetornarVistaIndexYModelo() throws Exception {
        // GIVEN
        DestinoDTO destino1 = new DestinoDTO(); 
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
        mockMvc.perform(get("/pago"))
                .andExpect(status().isOk())
                .andExpect(view().name("pago"));
    }

    @Test
    void testDetalleAlojamientos_DeberiaRetornarVistaDetalle() throws Exception {
        mockMvc.perform(get("/detalleAlojamientos.html"))
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