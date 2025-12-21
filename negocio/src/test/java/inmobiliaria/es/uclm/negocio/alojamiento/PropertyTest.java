package inmobiliaria.es.uclm.negocio.alojamiento;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class PropertyTest {

    @Test
    @DisplayName("onCreate: Sets default values correctly 🕒")
    void onCreate_SetsDefaultValues() {
        Alojamiento property = new Alojamiento();

        property.onCreate();

        assertNotNull(property.getCreatedAt());
        assertNotNull(property.getUpdatedAt());

        // CORRECCIÓN: Usamos getActive() porque el campo es Boolean (objeto)
        assertTrue(property.getActive(), "Should be active by default");
        assertEquals("ESTRICTA", property.getPoliticaCancelacion());
    }

    @Test
    @DisplayName("onCreate: Does NOT overwrite existing values 🛡️")
    void onCreate_DoesNotOverwrite() {
        Alojamiento property = new Alojamiento();
        property.setActive(false);
        property.setPoliticaCancelacion("FLEXIBLE");

        property.onCreate();

        // CORRECCIÓN: Usamos getActive()
        assertFalse(property.getActive(), "Should not change to true if it was false");
        assertEquals("FLEXIBLE", property.getPoliticaCancelacion());
        assertNotNull(property.getCreatedAt());
    }

    @Test
    @DisplayName("onUpdate: Updates modification timestamp 🔄")
    void onUpdate_UpdatesTimestamp() {
        Alojamiento property = new Alojamiento();
        property.onCreate();

        LocalDateTime pastDate = LocalDateTime.now().minusSeconds(5);
        property.setUpdatedAt(pastDate);

        property.onUpdate();

        assertTrue(property.getUpdatedAt().isAfter(pastDate));
    }

    @Test
    @DisplayName("Lombok: Verifies generated methods work ✅")
    void lombok_MethodsWork() {
        Alojamiento property = new Alojamiento();
        property.setNombre("Test House");

        assertEquals("Test House", property.getNombre());
        assertTrue(property.toString().startsWith("Alojamiento("));
    }
}