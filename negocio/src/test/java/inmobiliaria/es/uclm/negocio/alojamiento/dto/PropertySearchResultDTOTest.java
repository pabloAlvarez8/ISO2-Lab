package inmobiliaria.es.uclm.negocio.alojamiento.dto;

import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PropertySearchResultDTOTest {

    @Test
    @DisplayName("shouldMapFullEntityToDto_Correctly 🗺️")
    void shouldMapFullEntityToDto() {
        // 1. GIVEN: Una entidad con TODOS los datos rellenos
        Alojamiento entidad = new Alojamiento();
        entidad.setId(1L);
        entidad.setNombre("Casa Playa");
        entidad.setCiudad("Valencia");
        entidad.setTipo("Apartamento");
        entidad.setPrecio(new BigDecimal("150.50"));
        entidad.setFotoUrl("http://foto.com/img.jpg");
        entidad.setValoracionMedia(4.8);
        entidad.setCapacidad(4);
        entidad.setDistanciaCentro(new BigDecimal("2.5"));

        // 2. WHEN: Convertimos a DTO
        // Nota: Seguimos usando tu clase AlojamientoSearchResultDTO, solo cambia el nombre del Test
        AlojamientoSearchResultDTO dto = AlojamientoSearchResultDTO.fromEntity(entidad);

        // 3. THEN: Verificamos que todo coincide
        assertEquals(1L, dto.id());
        assertEquals("Casa Playa", dto.title());
        assertEquals("Valencia", dto.ciudad());
        assertEquals("Apartamento", dto.type());
        assertEquals(new BigDecimal("150.50"), dto.price());

        // Verificamos que la foto única se ha metido en una lista
        assertEquals(1, dto.images().size());
        assertEquals("http://foto.com/img.jpg", dto.images().getFirst());

        assertEquals(4.8, dto.rating());
        assertEquals(4, dto.capacity());
        assertEquals(new BigDecimal("2.5"), dto.distance());
    }

    @Test
    @DisplayName("shouldHandleNullValues_WithDefaults 🛡️")
    void shouldHandleNullValues_WithDefaults() {
        // 1. GIVEN: Una entidad con valores NULOS (casos peligrosos)
        Alojamiento entidad = new Alojamiento();
        entidad.setId(2L);
        entidad.setNombre("Casa Vacía");
        // Dejamos fotoUrl, valoracionMedia y distanciaCentro como NULL

        // 2. WHEN
        AlojamientoSearchResultDTO dto = AlojamientoSearchResultDTO.fromEntity(entidad);

        // 3. THEN: Verificamos que el DTO aplica los valores por defecto

        // Foto debe ser la de defecto
        assertFalse(dto.images().isEmpty());
        assertEquals("/images/no-image.png", dto.images().getFirst());

        // Valoración debe ser 0.0 (no null)
        assertNotNull(dto.rating());
        assertEquals(0.0, dto.rating());

        // Distancia debe ser 0 (no null)
        assertNotNull(dto.distance());
        assertEquals(BigDecimal.ZERO, dto.distance());
    }
}