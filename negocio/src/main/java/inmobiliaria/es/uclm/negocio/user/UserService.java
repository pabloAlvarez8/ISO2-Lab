package inmobiliaria.es.uclm.negocio.user;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// Importa BCryptPasswordEncoder correctamente
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    // Inyecta el Bean PasswordEncoder (forma recomendada)
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional // Buena práctica para métodos que modifican la BD
    public User registerUser(User user) {
        // Comprobar si el email ya existe (¡importante!)
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Intento de registro con email existente: {}", user.getEmail());
            // Deberías lanzar una excepción específica que el controlador pueda capturar
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }

        try {
            // Hashear la contraseña antes de guardar
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Establecer rol por defecto si no se proporciona (ya gestionado por el default
            // del Enum)
            if (user.getRole() == null) {
                user.setRole(User.Role.INQUILINO);
            }

            log.info("Intentando guardar usuario con email: {}", user.getEmail());
            log.info("--- VALORES ANTES DE SAVE ---");
            log.info("ID: {}", user.getId()); // Debería ser null
            log.info("Email: {}", user.getEmail()); // ¿Es null aquí?
            log.info("Nombre: {}", user.getNombre());
            log.info("Apellido: {}", user.getApellido());
            log.info("Password (Hashed): {}", user.getPassword());
            log.info("Direccion: {}", user.getDireccion());
            log.info("URL Foto: {}", user.getUrlFotoPerfil());
            log.info("Role: {}", user.getRole());
            log.info("-----------------------------");
            User savedUser = userRepository.save(user);
            log.info("✅ Usuario guardado correctamente con ID: {}", savedUser.getId());
            return savedUser;

        } catch (Exception e) {
            log.error("❌ Error al guardar usuario con email {}: {}", user.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Error ocurrido durante el registro del usuario.", e);
        }
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public boolean existsByEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);
        log.debug("Comprobando si el email {} existe: {}", email, exists);
        return exists;
    }

    // Añade otros métodos relacionados con usuarios aquí (login, findById, etc.)
}