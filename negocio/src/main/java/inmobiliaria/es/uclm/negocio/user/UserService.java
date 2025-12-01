package inmobiliaria.es.uclm.negocio.user;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Collections;

/**
 * Servicio que implementa la lógica de negocio para la gestión de usuarios.
 * <p>
 * Además de las operaciones CRUD básicas, implementa {@link UserDetailsService} para
 * integrarse con el mecanismo de autenticación de Spring Security.
 */
@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    /**
     * Repositorio para el acceso a datos de la entidad User.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Componente para el cifrado de contraseñas antes de su almacenamiento.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo usuario en el sistema.
     * <p>
     * Realiza las validaciones necesarias, cifra la contraseña y asigna un rol por defecto
     * antes de persistir la entidad. La operación es transaccional.
     *
     * @param user La entidad User con los datos del formulario.
     * @return El usuario persistido con su ID generado.
     * @throws IllegalArgumentException Si el correo electrónico ya existe en la base de datos.
     * @throws RuntimeException Si ocurre un error inesperado durante el proceso de guardado.
     */
    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Intento de registro con email existente: {}", user.getEmail());
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }
        try {
            // Cifrado de contraseña para seguridad en almacenamiento
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Asignación de rol por defecto si no viene informado
            if (user.getRole() == null) {
                user.setRole(User.Role.INQUILINO);
            }
            
            log.info("Guardando nuevo usuario: {}", user.getEmail());
            User savedUser = userRepository.save(user);
            log.info("Usuario guardado correctamente con ID: {}", savedUser.getId());
            return savedUser;

        } catch (Exception e) {
            log.error("Error al guardar usuario con email {}: {}", user.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Error ocurrido durante el registro del usuario.", e);
        }
    }

    /**
     * Busca un usuario por su dirección de correo electrónico.
     *
     * @param email El email del usuario a buscar.
     * @return Un {@link Optional} que contiene el usuario si existe, o vacío si no.
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Verifica si existe algún usuario registrado con el email proporcionado.
     *
     * @param email El email a verificar.
     * @return true si el email ya está en uso, false en caso contrario.
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Carga los detalles de un usuario para la autenticación de Spring Security.
     * <p>
     * Este método es invocado automáticamente por el framework durante el proceso de login.
     * Busca al usuario por su email (utilizado como username) y construye el objeto
     * {@link UserDetails} necesario para validar las credenciales.
     *
     * @param email El correo electrónico proporcionado en el formulario de login.
     * @return Un objeto {@link UserDetails} con la información de autenticación del usuario.
     * @throws UsernameNotFoundException Si no se encuentra ningún usuario con ese email.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        log.debug("Buscando usuario por email para autenticación: {}", email);

        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Intento de login fallido. Email no encontrado: {}", email);
                    return new UsernameNotFoundException("Usuario (email) no encontrado: " + email);
                });

        // Construcción del UserDetails de Spring Security usando el email como principal
        return new org.springframework.security.core.userdetails.User(
            usuario.getEmail(), 
            usuario.getPassword(), 
            Collections.emptyList() // Roles vacíos por el momento
        );
    }
}