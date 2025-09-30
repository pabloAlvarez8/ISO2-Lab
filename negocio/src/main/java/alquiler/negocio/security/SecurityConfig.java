package alquiler.negocio.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // ✅ permite acceder a CSS, JS, imágenes
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                // ✅ permite acceder al formulario de registro
                .requestMatchers("/register", "/api/users/register").permitAll()
                // todo lo demás requiere login
                .anyRequest().authenticated()
            )
            // desactiva CSRF solo para pruebas
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
