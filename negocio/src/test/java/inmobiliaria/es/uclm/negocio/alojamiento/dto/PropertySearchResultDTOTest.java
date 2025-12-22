package inmobiliaria.es.uclm.negocio.alojamiento.dto;

import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlojamientoSearchResultDTOTest {

    @Test
    @DisplayName("fromEntity: Maps full entity to DTO correctly 🗺️")
    void fromEntity_FullData_MapsCorrectly() {
        // 1. GIVEN
        // Usamos SPY para poder simular getValoracionMedia() sin rellenar la lista de valoraciones
        Alojamiento entity = spy(new Alojamiento()); 
        entity.setId(1L);
        entity.setNombre("Casa Playa");
        entity.setCiudad("Valencia");
        entity.setTipo("Apartamento");
        entity.setPrecio(new BigDecimal("150.50"));
        entity.setFotoUrl("http://foto.com/img.jpg");
        entity.setCapacidad(4);
        entity.setDistanciaCentro(new BigDecimal("2.5"));

        // Forzamos que la media sea 4.8 (ignorando la lógica interna de la lista vacía)
        doReturn(4.8).when(entity).getValoracionMedia();

        // 2. WHEN
        AlojamientoSearchResultDTO dto = AlojamientoSearchResultDTO.fromEntity(entity);

        // 3. THEN
        assertEquals(1L, dto.id());
        assertEquals("Casa Playa", dto.title());
        assertEquals("Valencia", dto.ciudad());
        assertEquals("Apartamento", dto.type());
        assertEquals(new BigDecimal("150.50"), dto.price());
        assertEquals(1, dto.images().size());
        assertEquals("http://foto.com/img.jpg", dto.images().getFirst());
        assertEquals(4.8, dto.rating());
        assertEquals(4, dto.capacity());
        assertEquals(new BigDecimal("2.5"), dto.distance());
    }

    @Test
    @DisplayName("fromEntity: Handles NULL values with defaults 🛡️")
    void fromEntity_NullValues_UsesDefaults() {
        // 1. GIVEN
        Alojamiento entity = new Alojamiento(); // Aquí objeto normal, la media será 0.0 por defecto
        entity.setId(2L);
        entity.setNombre("Casa Vacía");
        // Dejamos nulos: fotoUrl, distanciaCentro.
        // valoracionMedia será 0.0 porque la lista de valoraciones está vacía.

        // 2. WHEN
        AlojamientoSearchResultDTO dto = AlojamientoSearchResultDTO.fromEntity(entity);

        // 3. THEN
        // Image default
        assertFalse(dto.images().isEmpty());
        assertEquals("/images/no-image.png", dto.images().getFirst());

        // Rating default
        assertNotNull(dto.rating());
        assertEquals(0.0, dto.rating());

        // Distance default
        assertNotNull(dto.distance());
        assertEquals(BigDecimal.ZERO, dto.distance());
    }

    @Test
    @DisplayName("fromEntity: Handles EMPTY string image with default 🖼️")
    void fromEntity_EmptyImageString_UsesDefault() {
        // 1. GIVEN
        Alojamiento entity = new Alojamiento();
        entity.setFotoUrl(""); // Vacío pero no null

        // 2. WHEN
        AlojamientoSearchResultDTO dto = AlojamientoSearchResultDTO.fromEntity(entity);

        // 3. THEN
        assertEquals("/images/no-image.png", dto.images().getFirst());
    }
}