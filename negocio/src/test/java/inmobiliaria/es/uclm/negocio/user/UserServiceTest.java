package inmobiliaria.es.uclm.negocio.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // --- TEST REGISTRO DE USUARIO ---

    @Test
    void testRegisterUser_Success() {
        // Arrange (Preparación)
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act (Ejecución)
        User result = userService.registerUser(user);

        // Assert (Verificación)
        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword()); // La contraseña debe estar hasheada
        assertEquals(User.Role.INQUILINO, result.getRole()); // Debe asignar rol por defecto
        verify(userRepository).save(user);
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        // Arrange
        User user = new User();
        user.setEmail("existing@example.com");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(user);
        });

        assertEquals("El correo electrónico ya está registrado.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // --- TEST LOGIN (LoadUserByUsername) ---

    @Test
    void testLoadUserByUsername_Success() {
        // Arrange
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword("encodedPwd");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("encodedPwd", userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(email);
        });

        assertTrue(exception.getMessage().contains("Usuario (email) no encontrado"));
    }

    // --- TEST CONVERTIR EN ANFITRIÓN ---

    @Test
    void testConvertirEnAnfitrion_Success() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setRole(User.Role.INQUILINO);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.convertirEnAnfitrion(userId, "12345678A", "600123456", "ES1234567890");

        // Assert
        assertEquals("12345678A", user.getDni());
        assertEquals("600123456", user.getTelefono());
        assertEquals("ES1234567890", user.getCuentaBancaria());
        assertEquals(User.Role.PROPIETARIO, user.getRole());

        verify(userRepository).save(user);
    }

    @Test
    void testConvertirEnAnfitrion_UserNotFound() {
        // Arrange
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.convertirEnAnfitrion(userId, "dni", "tfno", "iban");
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
    }
}