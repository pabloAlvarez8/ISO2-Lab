package inmobiliaria.es.uclm.negocio.user;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.GrantedAuthority; // Importación necesaria
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Collections;
import java.util.Collection;

@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- Tu método de registro (sin cambios) ---
    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Intento de registro con email existente: {}", user.getEmail());
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }
        try {
            // Hashear la contraseña
            user.setPassword(passwordEncoder.encode(user.getPassword()));

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
            throw new RuntimeException("Error ocurrido durante el registro del usuario.", e);
        }
    }

    // --- Tus otros métodos (sin cambios) ---
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // --- MÉTODO DE LOGIN (UserDetailsService) ---
    
    /**
     * Este es el método que Spring Security llama automáticamente durante el login.
     * Busca al usuario por EMAIL y devuelve un CustomUserDetails con el ID.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        log.debug("Buscando usuario por email para autenticación: {}", email);

        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Intento de login fallido. Email no encontrado: {}", email);
                    return new UsernameNotFoundException("Usuario (email) no encontrado: " + email);
                });

        // --- CAMBIO PRINCIPAL ---
        // Devolvemos nuestra clase personalizada que sí tiene el campo ID
        return new CustomUserDetails(
            usuario.getId(),       // Pasamos el ID de la base de datos
            usuario.getEmail(),    // Email como username
            usuario.getPassword(), // Contraseña hasheada
            Collections.emptyList() // Roles (puedes ajustarlo si usas roles reales)
        );
    }

    // --- CLASE INTERNA PARA GESTIONAR EL ID ---
    public static class CustomUserDetails extends org.springframework.security.core.userdetails.User {
        private final Long id; // Asumimos que tu ID es Long (ajusta si es String o Integer)

        public CustomUserDetails(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
            super(username, password, authorities);
            this.id = id;
        }

        public Long getId() {
            return id;
        }
    }
}