package inmobiliaria.es.uclm.negocio.alojamiento;

// Imports para el controlador
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Imports para la autenticación y el guardado
import org.springframework.security.core.Authentication;
import inmobiliaria.es.uclm.negocio.user.User;
import inmobiliaria.es.uclm.negocio.user.UserService;

@Controller
@RequestMapping("/alojamientos")
public class AlojamientoController {

    private final AlojamientoService_Interfaz alojamientoService;
    private final UserService userService;

    // Constructor para inyectar los servicios
    public AlojamientoController(AlojamientoService_Interfaz alojamientoService, UserService userService) {
        this.alojamientoService = alojamientoService;
        this.userService = userService;
    }

    /**
     * Muestra la página de resultados de búsqueda (Buscador.html).
     * Esta versión NO busca en la BD. Simplemente sirve el HTML.
     * El JavaScript dentro de "Buscador.html" se encargará de
     * leer los parámetros de la URL y llamar a la API (/api/alojamientos).
     */
    @GetMapping
    public String mostrarPaginaDeBusqueda(
            // (Opcional) Pasamos los filtros al modelo para que los <input>
            // puedan mostrar los valores que venían en la URL.
            @RequestParam(value = "q", required = false) String ciudad,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "people", required = false, defaultValue = "1") int capacity,
            Model model) {

        // Ya NO se llama a alojamientoService.buscarConFiltros() aquí.

        // Solo pasamos los filtros iniciales de la URL al HTML
        model.addAttribute("filtroCiudad", ciudad);
        model.addAttribute("filtroTipo", type);
        model.addAttribute("filtroCapacidad", capacity);

        return "Buscador"; // Devuelve la plantilla 'Buscador.html'
    }

    /**
     * Muestra el formulario para crear un nuevo alojamiento.
     */
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("alojamiento", new Alojamiento());
        return "form-alojamiento";
    }

    /**
     * Procesa el guardado del nuevo alojamiento desde el formulario.
     */
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Alojamiento alojamiento, Authentication authentication) {

        // Busca al usuario propietario que ha iniciado sesión
        String userEmail = authentication.getName();
        User anfitrion = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Asigna el anfitrión al alojamiento antes de guardarlo
        alojamiento.setAnfitrion(anfitrion);
        alojamientoService.guardar(alojamiento);

        return "redirect:/alojamientos"; // Redirige a la página de búsqueda
    }

    /**
     * Elimina un alojamiento.
     */
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        alojamientoService.eliminar(id);
        return "redirect:/alojamientos";
    }

    /**
     * Muestra la página de detalle (que cargará datos desde el localStorage).
     */
    @GetMapping("/detalleAlojamientos")
    public String detalleAlojamientos() {
        return "detalleAlojamientos";
    }
}