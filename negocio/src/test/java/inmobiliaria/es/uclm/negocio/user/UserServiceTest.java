package inmobiliaria.es.uclm.negocio.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
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

    // REGISTRO TESTS
    @Test
    @DisplayName("registerUser: Success flow ✅")
    void registerUser_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("rawPassword");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedHash");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        User result = userService.registerUser(user);

        assertNotNull(result);
        assertEquals("encodedHash", result.getPassword());
        assertEquals(User.Role.INQUILINO, result.getRole());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("registerUser: If role exists, do not overwrite 🛡️")
    void registerUser_ExistingRole_DoesNotOverwrite() {
        User user = new User();
        user.setEmail("admin@test.com");
        user.setPassword("pass");
        user.setRole(User.Role.PROPIETARIO);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        // Configuramos el mock para que devuelva el usuario guardado
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(user);

        assertEquals(User.Role.PROPIETARIO, result.getRole());
    }

    @Test
    @DisplayName("registerUser: Email exists throws IllegalArgumentException 🚫")
    void registerUser_EmailAlreadyExists_ThrowsException() {
        User user = new User();
        user.setEmail("existing@example.com");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser(user)
        );

        assertEquals("El correo electrónico ya está registrado.", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("registerUser: Database error throws ResponseStatusException 💥")
    void registerUser_DbError_ThrowsResponseStatusException() {
        User user = new User();
        user.setEmail("error@test.com");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenThrow(new RuntimeException("DB Connection failed"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                userService.registerUser(user)
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
        String reason = ex.getReason();
        assertNotNull(reason);
        assertTrue(reason.contains("Error ocurrido durante el registro"));
    }

    @Test
    @DisplayName("registerUser: Sets default role when null (Cover IF branch) ✅")
    void registerUser_RoleIsNull_SetsDefault() {
        // GIVEN
        User user = new User();
        user.setEmail("nullrole@test.com");
        user.setPassword("pass");
        user.setRole(null); // Explícitamente null para entrar en el IF

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        User result = userService.registerUser(user);

        // THEN
        assertEquals(User.Role.INQUILINO, result.getRole());
    }

    @Test
    @DisplayName("registerUser: Keeps existing role (Cover ELSE branch) 🛡️")
    void registerUser_RoleIsNotNull_SkipsIf() {
        // GIVEN
        User user = new User();
        user.setEmail("owner@test.com");
        user.setPassword("pass");
        user.setRole(User.Role.PROPIETARIO); // NO es null, debe saltar el IF

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        User result = userService.registerUser(user);

        // THEN
        assertEquals(User.Role.PROPIETARIO, result.getRole());
    }

    // LOGIN TESTS
    @Test
    @DisplayName("loadUserByUsername: Success returns CustomUserDetails ✅")
    void loadUserByUsername_Success() {
        String email = "user@example.com";
        User user = new User();
        user.setId(10L);
        user.setEmail(email);
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertInstanceOf(UserService.CustomUserDetails.class, userDetails);
        assertEquals(10L, ((UserService.CustomUserDetails) userDetails).getId());
        assertEquals(email, userDetails.getUsername());
    }

    @Test
    @DisplayName("loadUserByUsername: User not found throws exception 🔍")
    void loadUserByUsername_NotFound_ThrowsException() {
        String email = "ghost@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () ->
                userService.loadUserByUsername(email)
        );

        assertTrue(ex.getMessage().contains("Usuario (email) no encontrado"));
    }

    // ANFITRION TESTS
    @Test
    @DisplayName("convertirEnAnfitrion: Success updates role and data 🏠")
    void upgradeToHost_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setRole(User.Role.INQUILINO);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // IMPORTANTE: Simulamos que save() devuelve el usuario
        when(userRepository.save(user)).thenReturn(user);

        // CORRECCIÓN: Asignamos el resultado a una variable
        User updatedUser = userService.convertirEnAnfitrion(userId, "12345678A", "600123456", "ES1234");

        // Usamos la variable para los asserts
        assertNotNull(updatedUser);
        assertEquals("12345678A", updatedUser.getDni());
        assertEquals("600123456", updatedUser.getTelefono());
        assertEquals(User.Role.PROPIETARIO, updatedUser.getRole());

        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("convertirEnAnfitrion: User not found throws exception ⚠️")
    void upgradeToHost_NotFound_ThrowsException() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                userService.convertirEnAnfitrion(userId, "dni", "phone", "iban")
        );
    }

    // DELEGATE TESTS
    @Test
    @DisplayName("Simple lookups: delegate to repository 🔎")
    void simpleLookups_DelegateToRepo() {
        userService.findByEmail("a@b.com");
        verify(userRepository).findByEmail("a@b.com");

        userService.existsByEmail("a@b.com");
        verify(userRepository).existsByEmail("a@b.com");
    }

    // CUSTOM USER DETAILS TESTS
    @Test
    @DisplayName("CustomUserDetails: Equals Full Logic Coverage 🧩")
    void customUserDetails_Equals_FullCoverage() {
        // 1. SETUP: Instancia base
        UserService.CustomUserDetails u1 = new UserService.CustomUserDetails(
                1L, "user", "pass", Collections.emptyList()
        );

        // --- LÍNEA 1: if (this == o) return true; ---
        // Se prueba comparando el objeto consigo mismo
        assertEquals(u1, u1);

        // --- LÍNEA 2: if (o == null ... ) ---
        // Se prueba pasando null explícitamente.
        // Usamos assertFalse llamando directamente al método para asegurar que entra en TU código
        assertNotEquals(null, u1);

        // --- LÍNEA 2: ... || getClass() != o.getClass()) ---
        // Se prueba pasando un objeto de otra clase (ej. un String o un Object)
        assertNotEquals(u1, new Object());
        // --- LÍNEA 3: if (!super.equals(o)) return false; ---
        // ESTA ES LA DIFÍCIL. Necesitamos:
        // - Misma clase (CustomUserDetails)
        // - Distinto Username (para que el padre 'User' diga que son distintos)
        UserService.CustomUserDetails uDiffUsername = new UserService.CustomUserDetails(
                1L, "OTRO_USUARIO", "pass", Collections.emptyList()
        );
        assertNotEquals(u1, uDiffUsername);

        // --- LÍNEA FINAL: return Objects.equals(id, that.id); ---
        // Caso A: Mismo username, distinto ID (retorna false)
        UserService.CustomUserDetails uDiffId = new UserService.CustomUserDetails(
                2L, "user", "pass", Collections.emptyList()
        );
        assertNotEquals(u1, uDiffId);

        // Caso B: Todo igual (retorna true)
        UserService.CustomUserDetails u2 = new UserService.CustomUserDetails(
                1L, "user", "pass", Collections.emptyList()
        );
        assertEquals(u1, u2);

        // --- EXTRA: HashCode (para cumplir el contrato) ---
        assertEquals(u1.hashCode(), u2.hashCode());
        assertNotEquals(u1.hashCode(), uDiffId.hashCode());
    }
}