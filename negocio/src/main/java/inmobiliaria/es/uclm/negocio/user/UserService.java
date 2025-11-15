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

@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- Tu método de registro (ya está perfecto) ---
    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Intento de registro con email existente: {}", user.getEmail());
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }
        try {
            // Hashear la contraseña
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Asignar rol por defecto (aunque tu entidad ya lo hace, esto es una doble seguridad)
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

    // --- Tus otros métodos (findByEmail, existsByEmail) ---
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }


    // --- MÉTODO DE LOGIN (UserDetailsService) ---
    
    /**
     * Este es el método que Spring Security llama automáticamente durante el login.
     * Busca al usuario por EMAIL.
     *
     * @param email El email que el usuario escribió en el formulario.
     * @return un objeto UserDetails que Spring Security usa para verificar la contraseña.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        log.debug("Buscando usuario por email para autenticación: {}", email);

        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Intento de login fallido. Email no encontrado: {}", email);
                    return new UsernameNotFoundException("Usuario (email) no encontrado: " + email);
                });

        // --- ¡AQUÍ ESTÁ EL ÚNICO CAMBIO! ---
        // Antes: usuario.getUsername()
        // Ahora: usuario.getEmail()
        //
        // (Lombok nos da getEmail() y getPassword() automáticamente)
        return new org.springframework.security.core.userdetails.User(
            
            usuario.getEmail(), // Usamos el email como el "principal"
            
            usuario.getPassword(), // Spring se encarga de comparar este hash
            
            Collections.emptyList() // Lista de roles (vacía por ahora)
        );
    }
}