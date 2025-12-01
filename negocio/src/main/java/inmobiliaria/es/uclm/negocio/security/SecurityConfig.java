package inmobiliaria.es.uclm.negocio.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Clase de configuración principal de Spring Security.
 * Define las políticas de autorización, la gestión de sesiones, el cifrado de contraseñas
 * y la integración con el formulario de login personalizado.
 */
@Configuration
public class SecurityConfig {

    /**
     * Define el bean encargado de codificar y verificar las contraseñas.
     * Se utiliza BCrypt, un algoritmo de hashing robusto estándar en la industria.
     * * @return Una instancia de {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     * Aquí se establecen qué rutas son públicas, se configura el comportamiento del
     * formulario de login y se definen las redirecciones de éxito o fracaso.
     *
     * @param http El objeto {@link HttpSecurity} para construir la configuración.
     * @return La cadena de filtros (SecurityFilterChain) configurada.
     * @throws Exception Si ocurre un error durante la construcción de la seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitamos CSRF para simplificar el desarrollo y pruebas (no recomendado en prod sin análisis)
                .csrf(AbstractHttpConfigurer::disable)

                // Definición de reglas de acceso por ruta
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login",    // Vista del formulario de login
                                "/register", // Vista y proceso de registro
                                "/css/**",   // Recursos estáticos
                                "/js/**")
                        .permitAll() // Estas rutas son accesibles por cualquiera
                        .anyRequest().authenticated()) // Cualquier otra ruta requiere login
                
                // Configuración del Login con formulario
                .formLogin(form -> form
                        .loginPage("/login")           // Nuestra vista personalizada
                        .loginProcessingUrl("/login")  // La URL donde Spring espera el POST
                        .defaultSuccessUrl("/dashboard", true) // Redirección tras login correcto
                        .failureUrl("/login?error=true")       // Redirección tras fallo
                        .permitAll())
                
                // Configuración del Logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true") // Redirección tras salir
                        .permitAll())

                // Configuración necesaria para permitir la consola H2 en frames
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}