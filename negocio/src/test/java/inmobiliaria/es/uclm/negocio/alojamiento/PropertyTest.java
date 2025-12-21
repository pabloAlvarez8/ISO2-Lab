package inmobiliaria.es.uclm.negocio.alojamiento;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class PropertyTest {

    // TEST 1: Forzamos NULL para que entre en los IF
    @Test
    @DisplayName("onCreate: Sets default values when fields are null 🕒")
    void onCreate_NullFields_SetsDefaults() {
        Alojamiento property = new Alojamiento();

        // ¡IMPORTANTE! Como en tu clase definiste 'private Boolean active = true',
        // por defecto NO es null. Tenemos que forzarlo a null para probar el IF.
        property.setActive(null);

        // También forzamos updatedAt a null por si acaso (aunque suele serlo)
        property.setUpdatedAt(null);

        // WHEN
        property.onCreate();

        // THEN
        assertNotNull(property.getCreatedAt());
        assertNotNull(property.getUpdatedAt());

        // Ahora sí hemos probado la línea "active = true"
        assertTrue(property.getActive());
        assertEquals("ESTRICTA", property.getPoliticaCancelacion());
    }

    // TEST 2: Lógica de NO sobrescribir
    @Test
    @DisplayName("onCreate: Respects existing values (Does not overwrite) 🛡️")
    void onCreate_ExistingValues_DoesNotOverwrite() {
        Alojamiento property = new Alojamiento();
        LocalDateTime manualDate = LocalDateTime.of(2020, 1, 1, 10, 0);
        property.setUpdatedAt(manualDate);
        property.setPoliticaCancelacion("FLEXIBLE");
        property.setActive(false); // Aquí active NO es null, es false. Salta el IF.

        property.onCreate();

        assertEquals(manualDate, property.getUpdatedAt());
        assertFalse(property.getActive());
        assertEquals("FLEXIBLE", property.getPoliticaCancelacion());
        assertNotNull(property.getCreatedAt());
    }

    // TEST 3: Lógica de actualización
    @Test
    @DisplayName("onUpdate: Updates timestamp logic 🔄")
    void onUpdate_Logic() {
        Alojamiento property = new Alojamiento();
        property.onCreate();
        LocalDateTime oldDate = LocalDateTime.now().minusSeconds(1);
        property.setUpdatedAt(oldDate);

        property.onUpdate();

        assertTrue(property.getUpdatedAt().isAfter(oldDate));
    }

    // TEST 4: Comprobación de Lombok
    @Test
    @DisplayName("Lombok: Verifies generated methods work ✅")
    void lombok_MethodsWork() {
        Alojamiento property = new Alojamiento();
        property.setNombre("Test House");

        assertEquals("Test House", property.getNombre());
        assertTrue(property.toString().startsWith("Alojamiento("));
    }
}