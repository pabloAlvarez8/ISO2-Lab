package inmobiliaria.es.uclm.negocio.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginWebController {

    /**
     * Muestra la página de login (login.html).
     * * Este método solo sirve la página HTML. Spring Security se encarga
     * de mostrar los mensajes de error (?error=true) o de logout (?logout=true)
     * si se configuran en el SecurityConfig.
     */
    @GetMapping("/login")
    public String showLoginPage() {
        // Simplemente devuelve el nombre de la plantilla "login.html"
        // que está en src/main/resources/templates/
        return "login";
    }

    // --- ¡NO SE NECESITA UN @PostMapping("/login")! ---
    //
    // A diferencia del registro, no necesitamos un método para procesar
    // el login. Spring Security intercepta la petición POST a /login
    // automáticamente y llama a tu UserService (UserDetailsService)
    // para validar al usuario.
}