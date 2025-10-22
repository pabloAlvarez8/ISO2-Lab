package inmobiliaria.es.uclm.negocio.registro; // O tu paquete de controladores

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(RegistroWebController.class);

    @Autowired
    private UserService userService;

    // GET -> muestra el formulario vacío
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        // Asegura que siempre haya un objeto 'user' en el modelo para el formulario
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "register"; // Asume que tienes una plantilla register.html
    }


    // POST -> procesa el formulario
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, // Vincula datos del form al objeto User
                               RedirectAttributes redirectAttrs) {
       // ▼▼▼ ¡AÑADE ESTAS DOS LÍNEAS AQUÍ! ▼▼▼
        log.info("Datos recibidos del formulario - Email: {}", user.getEmail());
        log.info("Datos recibidos del formulario - Objeto User completo: {}", user); // Necesitas un método toString() en User.java para que esto sea útil

        try {
            userService.registerUser(user);
            redirectAttrs.addFlashAttribute("successMessage", "Usuario registrado correctamente.");
            return "redirect:/register";

        } catch (IllegalArgumentException e) {
            log.warn("Registro fallido para email {}: {}", user.getEmail(), e.getMessage());
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttrs.addFlashAttribute("user", user);
            return "redirect:/register";

        } catch (Exception e) {
            log.error("Error inesperado durante registro para email {}: {}", user.getEmail(), e.getMessage(), e);
            redirectAttrs.addFlashAttribute("errorMessage", "Error inesperado durante el registro.");
            redirectAttrs.addFlashAttribute("user", user);
            return "redirect:/register";
        }
    }
}