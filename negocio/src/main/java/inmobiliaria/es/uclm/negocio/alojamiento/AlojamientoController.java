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

/**
 * Controlador MVC encargado de la navegación y la gestión de vistas HTML para alojamientos.
 * <p>
 * A diferencia del {@code AlojamientoApiController}, esta clase no devuelve JSON,
 * sino que prepara los modelos de datos y resuelve las plantillas (Thymeleaf)
 * que se mostrarán al usuario final.
 * </p>
 */
@Controller
@RequestMapping("/alojamientos")
public class AlojamientoController {

    private final AlojamientoService_Interfaz alojamientoService;
    private final UserService userService;

    /**
     * Constructor con inyección de dependencias.
     * @param alojamientoService Servicio para operaciones con alojamientos.
     * @param userService Servicio para recuperar datos del usuario en sesión.
     */
    public AlojamientoController(AlojamientoService_Interfaz alojamientoService, UserService userService) {
        this.alojamientoService = alojamientoService;
        this.userService = userService;
    }

    /**
     * Carga la vista principal del buscador de alojamientos.
     * <p>
     * <strong>Nota de diseño:</strong> Este método no realiza la consulta a la base de datos.
     * Simplemente inicializa la vista ('Buscador.html') y pasa los parámetros de la URL
     * al modelo para mantener el estado de los filtros en la interfaz. La búsqueda real
     * se delega al cliente (JavaScript), que consumirá la API REST posteriormente.
     * </p>
     *
     * @param ciudad   Valor actual del filtro de ciudad (opcional).
     * @param type     Valor actual del filtro de tipo (opcional).
     * @param capacity Valor actual del filtro de capacidad (por defecto 1).
     * @param model    Modelo de Spring para pasar datos a la vista.
     * @return Nombre lógico de la vista ("Buscador").
     */
    @GetMapping
    public String mostrarPaginaDeBusqueda(
            @RequestParam(value = "q", required = false) String ciudad,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "people", required = false, defaultValue = "1") int capacity,
            Model model) {

        // Pasamos los filtros recibidos de vuelta a la vista para que los campos
        // del formulario aparezcan rellenos (User Experience).
        model.addAttribute("filtroCiudad", ciudad);
        model.addAttribute("filtroTipo", type);
        model.addAttribute("filtroCapacidad", capacity);

        return "Buscador";
    }

    /**
     * Prepara y muestra el formulario de alta para un nuevo alojamiento.
     * Inicializa un objeto vacío para el binding del formulario.
     *
     * @param model Modelo para insertar la instancia vacía.
     * @return Nombre de la vista del formulario ("form-alojamiento").
     */
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("alojamiento", new Alojamiento());
        return "form-alojamiento";
    }

    /**
     * Procesa el envío (submit) del formulario de creación de alojamiento.
     * <p>
     * Este método intercepta la solicitud POST, recupera al usuario actualmente
     * autenticado mediante Spring Security y lo asocia automáticamente como
     * anfitrión del nuevo inmueble antes de persistirlo.
     * </p>
     *
     * @param alojamiento Objeto populado con los datos del formulario.
     * @param authentication Contexto de seguridad para identificar al usuario actual.
     * @return Redirección a la lista de alojamientos tras el guardado exitoso.
     */
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Alojamiento alojamiento, Authentication authentication) {

        // Recuperación del usuario en sesión (Anfitrión)
        String userEmail = authentication.getName();
        User anfitrion = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Error crítico: Usuario en sesión no encontrado en BD"));

        // Vinculación de la entidad y persistencia
        alojamiento.setAnfitrion(anfitrion);
        alojamientoService.guardar(alojamiento);

        return "redirect:/alojamientos";
    }

    /**
     * Gestiona la eliminación de un alojamiento existente.
     *
     * @param id Identificador del alojamiento a eliminar.
     * @return Redirección a la vista principal tras la operación.
     */
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        alojamientoService.eliminar(id);
        return "redirect:/alojamientos";
    }

    /**
     * Sirve la vista de detalle de un alojamiento.
     * <p>
     * Esta vista actúa como contenedor ("shell"). Los datos específicos del alojamiento
     * se renderizan en el cliente utilizando datos almacenados localmente (localStorage)
     * o mediante llamadas asíncronas adicionales.
     * </p>
     *
     * @return Nombre de la vista de detalle.
     */
    @GetMapping("/detalleAlojamientos")
    public String detalleAlojamientos() {
        return "detalleAlojamientos";
    }
}