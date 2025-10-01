package alquiler.negocio.controller;

import alquiler.negocio.entity.User;
import alquiler.negocio.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebController {

    @Autowired
    private UserService userService;

    // GET -> muestra el formulario vacío
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User()); // para enlazar con el form
        return "register";
    }

    // POST -> procesa el formulario y vuelve a la misma página
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        userService.registerUser(user);
        model.addAttribute("user", new User()); // reinicia el form vacío
        model.addAttribute("successMessage", "Usuario registrado");
        return "register"; // vuelve a mostrar register.html
    }
}
