package inmobiliaria.es.uclm.negocio.user;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority; // Importación necesaria
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Collections;
import java.util.Objects;
import java.util.Collection;

@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // REGISTRO

    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Intento de registro con email existente: {}", user.getEmail());
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }
        try {
            // Hashear la contraseña
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Asignar rol por defecto (INQUILINO)
            // Asignar rol por defecto
            if (user.getRole() == null) {
                user.setRole(User.Role.INQUILINO);
            }

            log.info("Guardando nuevo usuario: {}", user.getEmail());
            User savedUser = userRepository.save(user);
            log.info("✅ Usuario guardado correctamente con ID: {}", savedUser.getId());
            return savedUser;

        } catch (Exception e) {
            log.error("❌ Error al guardar usuario con email {}: {}", user.getEmail(), e.getMessage(), e);

            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error ocurrido durante el registro del usuario.", 
                e
            );
        }
    }

    // --- MÉTODOS DE BÚSQUEDA ---
    // --- Tus otros métodos (sin cambios) ---
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // MÉTODO DE LOGIN (UserDetailsService)

    /**
     * Este es el método que Spring Security llama automáticamente durante el login.
     * Busca al usuario por EMAIL y devuelve un CustomUserDetails con el ID.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.debug("Buscando usuario por email para autenticación: {}", email);

        // 1. Buscamos el usuario en tu base de datos
        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Intento de login fallido. Email no encontrado: {}", email);
                    return new UsernameNotFoundException("Usuario (email) no encontrado: " + email);
                });

        // 2. IMPORTANTE: Devolvemos nuestra clase personalizada con el ID
        // Pasamos: usuario.getId(), usuario.getEmail(), usuario.getPassword() y los roles
        return new CustomUserDetails(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getPassword(),
                Collections.emptyList() // Aquí podrías mapear usuario.getRole() si lo necesitas más adelante
        );
    }

    // CONVERTIR USUARIO EN ANFITRIÓN)
    @Transactional
    public void convertirEnAnfitrion(Long idUsuario, String dni, String telefono, String iban) {
        User usuario = userRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizamos sus datos nuevos
        usuario.setDni(dni);
        usuario.setTelefono(telefono);
        usuario.setCuentaBancaria(iban);

        // Cambiamos el rol
        usuario.setRole(User.Role.PROPIETARIO);

        // GUARDAMOS EN BASE DE DATOS
        userRepository.save(usuario);

        log.info("Usuario {} actualizado a PROPIETARIO con datos completos.", usuario.getEmail());
    }

    // CLASE INTERNA PARA GESTIONAR EL ID
    public static class CustomUserDetails extends org.springframework.security.core.userdetails.User {
        private final Long id; // Asumimos que tu ID es Long (ajusta si es String o Integer)

        public CustomUserDetails(Long id, String username, String password,
                Collection<? extends GrantedAuthority> authorities) {
            super(username, password, authorities);
            this.id = id;
        }

        public Long getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            // 1. Same reference check
            if (this == o) return true;
            
            // 2. Class type check
            if (o == null || getClass() != o.getClass()) return false;
            
            // 3. Parent equality check (checks username and authorities)
            if (!super.equals(o)) return false;
            
            // 4. Custom field check (ID)
            CustomUserDetails that = (CustomUserDetails) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            // Combine parent hash (username) with ID hash
            return Objects.hash(super.hashCode(), id);
        }
    }
}