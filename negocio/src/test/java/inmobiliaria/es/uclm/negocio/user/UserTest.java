package inmobiliaria.es.uclm.negocio.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

// Usamos esta extensión para habilitar Mockito en JUnit 5
@ExtendWith(MockitoExtension.class)
class UserTest {

    @Mock
    private User userMock;

    @Test
    @DisplayName("Test básico de Getters y Setters y Enum")
    void testUserState() {
        // 1. Arrange (Preparar)
        User user = new User();
        String email = "test@uclm.es";
        String nombre = "Pepito";
        User.Role role = User.Role.PROPIETARIO;

        // 2. Act (Actuar)
        user.setEmail(email);
        user.setNombre(nombre);
        user.setRole(role);

        // 3. Assert (Verificar)
        assertEquals(email, user.getEmail());
        assertEquals(nombre, user.getNombre());
        assertEquals(User.Role.PROPIETARIO, user.getRole());
        // Verificamos que el valor por defecto sea INQUILINO si creamos otro
        assertEquals(User.Role.INQUILINO, new User().getRole());
    }

    @Test
    @DisplayName("Test de @PrePersist (onCreate): Debe asignar fechas de creación")
    void testOnCreate() {
        // Arrange
        User user = new User();
        
        // Verificamos que al principio son null
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());

        // Act - Llamamos al método manualmente (simulando lo que hace Hibernate)
        // Nota: Como onCreate es protected, este test debe estar en el mismo paquete
        user.onCreate();

        // Assert
        assertNotNull(user.getCreatedAt(), "createdAt no debería ser nulo tras persistir");
        assertNotNull(user.getUpdatedAt(), "updatedAt no debería ser nulo tras persistir");
        
        // Verificamos que createdAt y updatedAt sean prácticamente iguales al crearse
        assertEquals(user.getCreatedAt().getMinute(), user.getUpdatedAt().getMinute());
    }

    @Test
    @DisplayName("Test de @PreUpdate (onUpdate): Debe actualizar la fecha de modificación")
    void testOnUpdate() throws InterruptedException {
        // Arrange
        User user = new User();
        user.onCreate(); // Inicializamos fechas
        LocalDateTime fechaCreacionOriginal = user.getCreatedAt();
        LocalDateTime fechaUpdateOriginal = user.getUpdatedAt();

        // Pausa pequeña para asegurar que el tiempo cambie (solo para el test)
        Thread.sleep(100);

        // Act
        user.onUpdate();

        // Assert
        assertEquals(fechaCreacionOriginal, user.getCreatedAt(), "La fecha de creación NO debe cambiar");
        assertTrue(user.getUpdatedAt().isAfter(fechaUpdateOriginal), "La fecha de update debe ser posterior a la original");
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

    /**
     * Ejemplo con MOCKITO tal como pediste.
     * Útil si quieres simular un User sin instanciarlo completamente,
     * aunque para Entidades simples se recomienda usar 'new User()'.
     */
    @Test
    @DisplayName("Ejemplo usando Mockito para simular comportamiento")
    void testWithMockito() {
        // Definimos el comportamiento del Mock
        when(userMock.getId()).thenReturn(99L);
        when(userMock.getEmail()).thenReturn("mock@uclm.es");

        // Verificamos
        assertEquals(99L, userMock.getId());
        assertEquals("mock@uclm.es", userMock.getEmail());
        
        // Ojo: userMock.getNombre() devolverá null porque no lo hemos definido
        assertNull(userMock.getNombre()); 
    }
}