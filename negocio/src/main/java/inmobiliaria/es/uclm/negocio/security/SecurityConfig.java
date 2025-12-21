package inmobiliaria.es.uclm.negocio.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    public static final String LOGIN_URL = "/login";

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Correcto: lo necesitas para que el login pueda verificar el hash
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. DESHABILITA CSRF:
                // Esto elimina los "problemas" al probar con Postman o formularios simples.
                .csrf(AbstractHttpConfigurer::disable)

                // 2. DEFINE QUÉ ES PÚBLICO Y QUÉ ES PRIVADO:
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                        "/","/index",
                                "/error",
                                LOGIN_URL, // <-- Ruta de tu LoginWebController
                                "/register",
                                "/buscador",
                                "/alojamientos/detalleAlojamientos",
                                "/alojamientos/**",
                                "/api/alojamientos/**",
                                "/css/**", "/js/**", "/images/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        // ▼▼▼ AÑADE ESTA LÍNEA ▼▼▼
                        .loginPage(LOGIN_URL) // Le dice a Spring que use tu @GetMapping("/login")

                        // Esta es la URL que Spring intercepta para el POST
                        .loginProcessingUrl(LOGIN_URL)

                        // A dónde ir si el login es exitoso
                        .defaultSuccessUrl("/dashboard", true)

                        // A dónde ir si el login falla (Thymeleaf lo usará)
                        .failureUrl("/login?error=true")

                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        // A dónde ir tras el logout (Thymeleaf lo usará)
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll())

                // Opcional: Permite que la consola H2 funcione
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}