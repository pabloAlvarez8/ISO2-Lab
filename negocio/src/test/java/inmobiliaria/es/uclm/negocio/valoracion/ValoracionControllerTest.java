package inmobiliaria.es.uclm.negocio.valoracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
// Importante para Spring Boot 3.4+
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ValoracionController.class)
@AutoConfigureMockMvc(addFilters = false)
class ValoracionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ValoracionService valoracionService;

    @Autowired
    private ObjectMapper objectMapper; // Para convertir Map -> JSON String

    // --------------------------------------------------------
    // Test GET: Listar comentarios de un inmueble
    // --------------------------------------------------------
    @Test
    void listar_DeberiaDevolverListaJSON() throws Exception {
        // GIVEN: El servicio devuelve lista vacía
        when(valoracionService.obtenerPorAlojamiento(10L))
                .thenReturn(Collections.emptyList());

        // WHEN & THEN
        mockMvc.perform(get("/valoraciones/inmueble/10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]")); // Array vacío JSON
    }

    // --------------------------------------------------------
    // Test POST: Guardar Valoración (Éxito)
    // --------------------------------------------------------
    @Test
    void guardar_DatosValidos_DeberiaDevolver200OK() throws Exception {
        // GIVEN
        Map<String, Object> respuestaServicio = new HashMap<>();
        respuestaServicio.put("nuevaMedia", 4.5);
        respuestaServicio.put("status", "ok"); // Ejemplo de dato extra

        when(valoracionService.guardarValoracion(eq(10L), eq(1L), eq(5.0), anyString()))
                .thenReturn(respuestaServicio);

        // Payload enviado por el frontend (Map<String, Object>)
        Map<String, Object> payload = new HashMap<>();
        payload.put("inmuebleId", 10);
        payload.put("usuarioId", 1);
        payload.put("puntuacion", 5.0);
        payload.put("comentario", "Top");

        // WHEN & THEN
        mockMvc.perform(post("/valoraciones/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))) // Serializamos el mapa a JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nuevaMedia").value(4.5));
    }

    // --------------------------------------------------------
    // Test POST: Error (Usuario no ha visitado)
    // --------------------------------------------------------
    @Test
    void guardar_UsuarioNoAutorizado_DeberiaDevolver403() throws Exception {
        // GIVEN: El servicio lanza la excepción esperada
        String mensajeError = "Para poder escribir una reseña de este alojamiento antes tienes que visitarlo.";
        
        when(valoracionService.guardarValoracion(anyLong(), anyLong(), anyDouble(), anyString()))
                .thenThrow(new RuntimeException(mensajeError));

        Map<String, Object> payload = new HashMap<>();
        payload.put("inmuebleId", 10);
        payload.put("usuarioId", 1);
        payload.put("puntuacion", 1.0);
        payload.put("comentario", "Fake");

        // WHEN & THEN
        mockMvc.perform(post("/valoraciones/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                // Verificamos que tu Controller captura la excepción y devuelve FORBIDDEN (403)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensaje").value(mensajeError));
    }
}