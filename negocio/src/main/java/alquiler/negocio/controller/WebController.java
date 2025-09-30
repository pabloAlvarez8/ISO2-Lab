package alquiler.negocio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/register")
    public String showRegisterPage() {
        // busca register.html dentro de /templates
        return "register";
    }
}
