package inmobiliaria.es.uclm.negocio.alojamiento;

import inmobiliaria.es.uclm.negocio.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PropertyTest {

    @Test
    @DisplayName("onCreate should set default values (Active, Policy, Dates) when saving for the first time 🕒")
    void onCreate_NewEntity_SetsDefaultValues() {
        // 1. GIVEN (Dado una propiedad vacía)
        Alojamiento property = new Alojamiento();

        // 2. WHEN (Simulamos el guardado inicial llamando al método manualmente)
        property.onCreate();

        // 3. THEN (Verificamos que los campos se han rellenado)
        assertNotNull(property.getCreatedAt(), "La fecha de creación no debería ser nula");
        assertNotNull(property.getUpdatedAt(), "La fecha de actualización no debería ser nula");

        // Verificamos los valores por defecto
        assertTrue(property.isActive(), "La propiedad debería estar activa por defecto");
        assertEquals("ESTRICTA", property.getPoliticaCancelacion(), "La política de cancelación por defecto debería ser ESTRICTA");
    }

    @Test
    @DisplayName("onUpdate should refresh the UpdatedAt timestamp 🔄")
    void onUpdate_ExistingEntity_UpdatesTimestamp() throws InterruptedException {
        // 1. GIVEN
        Alojamiento property = new Alojamiento();
        property.onCreate(); // Inicializamos fechas
        LocalDateTime initialUpdateTime = property.getUpdatedAt();

        // Esperamos un poco para asegurar que el reloj del sistema avanza
        Thread.sleep(10);

        // 2. WHEN (Simulamos una actualización)
        property.onUpdate();

        // 3. THEN
        assertTrue(property.getUpdatedAt().isAfter(initialUpdateTime),
                "La fecha de actualización debería ser posterior a la original");
    }

    @Test
    @DisplayName("Should correctly store and retrieve monetary values (BigDecimal) 💶")
    void setPrice_ValidAmount_StoresCorrectValue() {
        // 1. GIVEN
        Alojamiento property = new Alojamiento();
        BigDecimal expectedPrice = new BigDecimal("150.50");

        // 2. WHEN
        property.setPrecio(expectedPrice);

        // 3. THEN
        assertEquals(expectedPrice, property.getPrecio(), "El precio guardado debería coincidir exactamente con el esperado");
    }

    @Test
    @DisplayName("Should maintain relationship with Host (User) 👤")
    void setHost_ValidUser_ReturnsUser() {
        // 1. GIVEN
        Alojamiento property = new Alojamiento();
        User hostMock = new User();
        hostMock.setId(1L);

        // 2. WHEN
        property.setAnfitrion(hostMock);

        // 3. THEN
        assertNotNull(property.getAnfitrion(), "El anfitrión no debería ser nulo");
        assertEquals(1L, property.getAnfitrion().getId(), "El ID del anfitrión debería coincidir");
    }

    @Test
    @DisplayName("Should handle manual boolean active status correctly ✅")
    void setActive_ManualOverride_ChangesStatus() {
        // 1. GIVEN
        Alojamiento property = new Alojamiento();
        property.onCreate(); // Por defecto es true

        // 2. WHEN
        property.setActive(false);

        // 3. THEN
        assertFalse(property.isActive(), "El estado debería ser falso después de cambiarlo manualmente");
    }
}