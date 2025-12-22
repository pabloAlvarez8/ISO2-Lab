package inmobiliaria.es.uclm.negocio.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserTest {

    @Mock
    private User userMock;

    @Test
    @DisplayName("Test básico de Getters y Setters y Enum")
    void testUserState() {
        User user = new User();
        String email = "test@uclm.es";
        String nombre = "Pepito";
        User.Role role = User.Role.PROPIETARIO;

        user.setEmail(email);
        user.setNombre(nombre);
        user.setRole(role);

        assertEquals(email, user.getEmail());
        assertEquals(nombre, user.getNombre());
        assertEquals(User.Role.PROPIETARIO, user.getRole());
        assertEquals(User.Role.INQUILINO, new User().getRole());
    }

    @Test
    @DisplayName("Test de @PrePersist (onCreate): Debe asignar fechas de creación")
    void testOnCreate() {
        User user = new User();
        
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());

        user.onCreate();

        assertNotNull(user.getCreatedAt(), "createdAt no debería ser nulo tras persistir");
        assertNotNull(user.getUpdatedAt(), "updatedAt no debería ser nulo tras persistir");
        
        assertEquals(user.getCreatedAt().getMinute(), user.getUpdatedAt().getMinute());
    }

    @Test
    @DisplayName("Test de @PreUpdate (onUpdate): Debe actualizar la fecha de modificación")
    void testOnUpdate() {
        // 1. Arrange
        User user = new User();
        user.onCreate(); // Esto pone createdAt y updatedAt a "AHORA"
        
        // TRUCO PARA EVITAR THREAD.SLEEP:
        // Simulamos manualmente que este usuario se creó hace 1 hora
        LocalDateTime pasado = LocalDateTime.now().minusHours(1);
        user.setCreatedAt(pasado);
        user.setUpdatedAt(pasado); // El último update también fue en el pasado

        // Guardamos la fecha antigua para comparar luego
        LocalDateTime fechaUpdateAntigua = user.getUpdatedAt();

        // 2. Act - Al ejecutar esto, updatedAt se actualiza al momento actual (AHORA)
        user.onUpdate();

        // 3. Assert
        // createdAt debe seguir siendo "pasado"
        assertEquals(pasado, user.getCreatedAt(), "La fecha de creación NO debe cambiar");
        
        // updatedAt (AHORA) debe ser posterior a fechaUpdateAntigua (PASADO)
        assertTrue(user.getUpdatedAt().isAfter(fechaUpdateAntigua), "La fecha de update debe ser posterior a la original");
    }

    @Test
    @DisplayName("Test del método toString")
    void testToString() {
        User user = new User();
        user.setId(1L);
        user.setEmail("correo@prueba.com");
        user.setRole(User.Role.INQUILINO);

        String esperado = "User{id=1, email='correo@prueba.com', role=INQUILINO}";
        assertEquals(esperado, user.toString());
    }

    @Test
    @DisplayName("Ejemplo usando Mockito para simular comportamiento")
    void testWithMockito() {
        when(userMock.getId()).thenReturn(99L);
        when(userMock.getEmail()).thenReturn("mock@uclm.es");

        assertEquals(99L, userMock.getId());
        assertEquals("mock@uclm.es", userMock.getEmail());
        assertNull(userMock.getNombre()); 
    }
}