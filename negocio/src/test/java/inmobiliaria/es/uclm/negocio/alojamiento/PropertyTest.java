package inmobiliaria.es.uclm.negocio.alojamiento;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PropertyTest {

    @Test
    @DisplayName("onCreate: Sets default values when fields are null 🕒")
    void onCreate_NullFields_SetsDefaults() {
        Alojamiento property = new Alojamiento();

        // Forzamos nulos para entrar en todas las ramas de los IF del método onCreate
        property.setActive(null);
        property.setUpdatedAt(null);
        property.setPoliticaCancelacion(null);

        // Simulación de persistencia (JPA llama a @PrePersist)
        property.onCreate();

        // Verificaciones
        assertNotNull(property.getCreatedAt(), "CreatedAt debería haberse generado");
        assertNotNull(property.getUpdatedAt(), "UpdatedAt debería haberse generado");
        assertTrue(property.getActive(), "Por defecto debería ser true");
        assertEquals("ESTRICTA", property.getPoliticaCancelacion(), "Política por defecto errónea");
    }

    @Test
    @DisplayName("onCreate: Respects existing values (Does not overwrite) 🛡️")
    void onCreate_ExistingValues_DoesNotOverwrite() {
        Alojamiento property = new Alojamiento();
        LocalDateTime manualDate = LocalDateTime.of(2020, 1, 1, 10, 0);

        property.setUpdatedAt(manualDate);
        property.setPoliticaCancelacion("FLEXIBLE");
        property.setActive(false);

        property.onCreate();

        // Verificamos que no se han sobrescrito los valores manuales
        assertEquals(manualDate, property.getUpdatedAt());
        assertFalse(property.getActive());
        assertEquals("FLEXIBLE", property.getPoliticaCancelacion());
        assertNotNull(property.getCreatedAt());
    }

    @Test
    @DisplayName("onUpdate: Updates timestamp logic 🔄")
    void onUpdate_Logic() {
        Alojamiento property = new Alojamiento();
        property.onCreate(); // Inicializa fechas

        LocalDateTime oldDate = LocalDateTime.now().minusDays(1);
        property.setUpdatedAt(oldDate);

        // Simulación de actualización (JPA llama a @PreUpdate)
        property.onUpdate();

        assertTrue(property.getUpdatedAt().isAfter(oldDate), "La fecha de actualización debería ser más reciente");
    }

    @Test
    @DisplayName("Lombok & Data: Verifies getters, setters and basic logic ✅")
    void lombok_MethodsWork() {
        Alojamiento property = new Alojamiento();
        BigDecimal precio = new BigDecimal("150.00");

        property.setId(1L);
        property.setNombre("Villa Real");
        property.setPrecio(precio);
        property.setCapacidad(4);
        property.setCiudad("Ciudad Real");

        assertEquals(1L, property.getId());
        assertEquals("Villa Real", property.getNombre());
        assertEquals(precio, property.getPrecio());
        assertEquals(4, property.getCapacidad());
        assertEquals("Ciudad Real", property.getCiudad());

        // Verifica que el método toString generado por Lombok no falle
        assertNotNull(property.toString());
        assertTrue(property.toString().contains("Villa Real"));
    }
}