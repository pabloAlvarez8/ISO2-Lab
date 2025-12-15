package inmobiliaria.es.uclm.negocio.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginWebController {

    @GetMapping("/login")
    public String showLoginPage(HttpServletRequest request, HttpSession session) {
        // 1. Obtener la URL desde la que viene el usuario
        String referrer = request.getHeader("Referer");

        // 2. Comprobamos que no sea null y que no sea la propia página de login
        // (para evitar un bucle si recargan la página de login)
        if (referrer != null && !referrer.contains("/login") && !referrer.contains("/register")) {
            session.setAttribute("urlPrevio", referrer);
        }

        return "login";
    }
}