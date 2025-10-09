package inmobiliaria.es.uclm.negocio.registro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import inmobiliaria.es.uclm.negocio.user.User;
import inmobiliaria.es.uclm.negocio.user.UserService;

@Controller
public class RegistroWebController {

    @Autowired
    private UserService userService;

    // GET -> muestra el formulario vacío
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "register";
    }


    // POST -> procesa el formulario y vuelve a la misma página
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, RedirectAttributes redirectAttrs) {
        if (userService.existsByEmail(user.getEmail())) { // evitar duplicados
            redirectAttrs.addFlashAttribute("errorMessage", "El usuario ya existe");
        } else {
            userService.registerUser(user);
            redirectAttrs.addFlashAttribute("successMessage", "Usuario registrado correctamente");
        }
        return "redirect:/register";
    }

}
