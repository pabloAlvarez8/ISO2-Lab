package inmobiliaria.es.uclm.negocio.registro;

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

/**
 * Controlador web para la gestión del proceso de registro de nuevos usuarios.
 * Mapea las peticiones HTTP GET y POST relacionadas con el formulario de registro.
 * Es una pieza fundamental en la capa de presentación que conecta la vista
 * con la lógica de negocio a través de {@link UserService}.
 */
@Controller
public class RegistroWebController {

    /**
     * Instancia de logger para registrar información y errores de la clase.
     */
    private static final Logger log = LoggerFactory.getLogger(RegistroWebController.class);

    /**
     * Inyección del servicio de negocio para la gestión de usuarios.
     */
    @Autowired
    private UserService userService;

    /**
     * Muestra el formulario de registro al manejar la petición GET "/register".
     *
     * En caso de redirección por error, asegura que los datos del usuario previamente introducidos
     * se mantengan en el modelo para evitar la pérdida de información.
     *
     * @param model El modelo de Spring MVC.
     * @return La vista del formulario de registro ("register").
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
     * Procesa los datos enviados desde el formulario de registro (petición POST a "/register").
     *
     * Intenta registrar el nuevo usuario mediante el servicio. En caso de éxito o fallo,
     * utiliza {@link RedirectAttributes} para enviar un mensaje a la siguiente vista
     * y redirige al formulario de registro para evitar el doble envío del formulario.
     *
     * @param user Objeto {@link User} poblado automáticamente con los datos del formulario.
     * @param redirectAttrs Utilidad para transferir mensajes (éxito/error) y datos (objeto User)
     * tras una redirección HTTP.
     * @return Redirección al formulario de registro ("/register").
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttrs) {
        
        log.info("Datos recibidos del formulario - Email: {}", user.getEmail());
        log.info("Datos recibidos del formulario - Objeto User completo: {}", user); 

        try {
            userService.registerUser(user);
            redirectAttrs.addFlashAttribute("successMessage", "Usuario registrado correctamente.");
            return "redirect:/register";

        } catch (IllegalArgumentException e) {
            log.warn("Registro fallido para email {}: {}", user.getEmail(), e.getMessage());
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
            // Devolvemos el usuario para no perder los datos que ya escribió
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