package inmobiliaria.es.uclm.negocio.registro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import inmobiliaria.es.uclm.negocio.user.User;
import inmobiliaria.es.uclm.negocio.user.UserService;

/**
 * Controlador web encargado de gestionar el proceso de registro de usuarios.
 *
 * Esta clase maneja las peticiones HTTP para mostrar el formulario de registro
 * y procesar los datos enviados por el usuario.
 *
 */
@Controller
public class RegistroWebController {

    private static final Logger log = LoggerFactory.getLogger(RegistroWebController.class);
    public static final String REDIRECT_REGISTER = "redirect:/register";

    /**
     * Servicio para la gestión de la lógica de negocio de usuarios.
     */
    private final UserService userService;

    /**
     * Constructor.
     * * @param userService El servicio de usuarios a inyectar.
     */
    public RegistroWebController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Maneja la petición GET para mostrar la página de registro.
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        // Asegura que siempre haya un objeto 'user' en el modelo para el formulario
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "register"; 
    }

    /**
     * Procesa la petición POST con los datos del formulario de registro.
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttrs) {
        
        log.info("Datos recibidos del formulario - Email: {}", user.getEmail());
        log.info("Datos recibidos del formulario - Objeto User completo: {}", user); 

        try {
            userService.registerUser(user);
            redirectAttrs.addFlashAttribute("successMessage", "Usuario registrado correctamente.");
            return REDIRECT_REGISTER;

        } catch (IllegalArgumentException e) {
            log.warn("Registro fallido para email {}: {}", user.getEmail(), e.getMessage());
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
            // Devolvemos el usuario para no perder los datos que ya escribió
            redirectAttrs.addFlashAttribute("user", user);
            return REDIRECT_REGISTER;

        } catch (Exception e) {
            log.error("Error inesperado durante registro para email {}: {}", user.getEmail(), e.getMessage(), e);
            redirectAttrs.addFlashAttribute("errorMessage", "Error inesperado durante el registro.");
            redirectAttrs.addFlashAttribute("user", user);
            return REDIRECT_REGISTER;
        }
    }
}