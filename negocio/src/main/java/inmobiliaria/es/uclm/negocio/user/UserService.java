package inmobiliaria.es.uclm.negocio.user;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import lombok.Getter;

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
            // CORRECCIÓN TYPO: 'Encriptar' en vez de 'Hashear'
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            if (user.getRole() == null) {
                user.setRole(User.Role.INQUILINO);
            }

            log.info("Guardando nuevo usuario: {}", user.getEmail());

            // CORRECCIÓN: Usamos el retorno de save()
            return userRepository.save(user);

        } catch (Exception e) {

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

    // MÉTODO DE LOGIN
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Buscando usuario por email para autenticación: {}", email);

        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Intento de login fallido. Email no encontrado: {}", email);
                    return new UsernameNotFoundException("Usuario (email) no encontrado: " + email);
                });

        return new CustomUserDetails(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getPassword(),
                Collections.emptyList()
        );
    }

    // CONVERTIR USUARIO EN ANFITRIÓN
    @Transactional
    public User convertirEnAnfitrion(Long idUsuario, String dni, String telefono, String iban) {
        User usuario = userRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setDni(dni);
        usuario.setTelefono(telefono);
        usuario.setCuentaBancaria(iban);
        usuario.setRole(User.Role.PROPIETARIO);

        log.info("Usuario {} actualizado a PROPIETARIO con datos completos.", usuario.getEmail());

        // CORRECCIÓN: Devolvemos el resultado de save() para evitar el warning "Return value ignored"
        return userRepository.save(usuario);
    }

    // CLASE INTERNA
    @Getter
    public static class CustomUserDetails extends org.springframework.security.core.userdetails.User {

        private final Long id;

        public CustomUserDetails(Long id, String username, String password,
                Collection<? extends GrantedAuthority> authorities) {
            super(username, password, authorities);
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            // NOTA: Ignora el aviso de "súper", es la palabra clave de Java, no español.
            if (!super.equals(o)) return false;

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