package inmobiliaria.es.uclm.negocio.alojamiento;

import inmobiliaria.es.uclm.negocio.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TODO: Refactorizar AlojamientoApiController a PropertyRestController en el código principal.
 */
@WebMvcTest(AlojamientoApiController.class)
class PropertyRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlojamientoService_Interfaz alojamientoService;

    @MockitoBean(name = "userService")
    private UserService userService;

    @Test
    @DisplayName("shouldReturnListOfDTOs_WhenFiltersAreApplied 🔍")
    @WithMockUser
    void search_WithFilters_ReturnsJsonList() throws Exception {
        // 1. GIVEN (Datos simulados)
        Alojamiento house = new Alojamiento();
        house.setId(10L);
        house.setNombre("Apartamento Sol"); // Se mapeará a 'title'
        house.setCiudad("Madrid");
        house.setTipo("Piso");              // Se mapeará a 'type'
        house.setPrecio(new BigDecimal("120.50")); // Se mapeará a 'price'
        house.setCapacidad(2);

        List<Alojamiento> mockResults = List.of(house);

        when(alojamientoService.buscarConFiltros(
                eq("Madrid"),
                any(),
                any(),
                any(),
                eq(2),
                anyString())
        ).thenReturn(mockResults);

        // 2. WHEN
        mockMvc.perform(get("/api/alojamientos")
                        .param("q", "Madrid")
                        .param("capacity", "2")
                        .contentType(MediaType.APPLICATION_JSON))

                // 3. THEN (Usando los nombres exactos del Record DTO)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(10)))
                .andExpect(jsonPath("$[0].title", is("Apartamento Sol")))
                .andExpect(jsonPath("$[0].ciudad", is("Madrid")))
                .andExpect(jsonPath("$[0].type", is("Piso")))
                .andExpect(jsonPath("$[0].price", is(120.50)))
                .andExpect(jsonPath("$[0].capacity", is(2)));
    }

    @Test
    @DisplayName("shouldUseDefaultValues_WhenNoParamsProvided ⚙️")
    @WithMockUser
    void search_NoParams_UsesDefaults() throws Exception {
        // 1. GIVEN
        when(alojamientoService.buscarConFiltros(any(), any(), any(), any(), anyInt(), anyString()))
                .thenReturn(Collections.emptyList());

        // 2. WHEN
        mockMvc.perform(get("/api/alojamientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // 3. THEN
        verify(alojamientoService).buscarConFiltros(
                null, null, null, null, 1, "recommend"
        );
    }
}