package inmobiliaria.es.uclm.negocio.alojamiento;

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

// Imports para valoraciones
import inmobiliaria.es.uclm.negocio.valoracion.ValoracionService;
import inmobiliaria.es.uclm.negocio.valoracion.ValoracionInmueble;

import java.util.List;

@Controller
@RequestMapping("/alojamientos")
public class AlojamientoController {

    private final AlojamientoService_Interfaz alojamientoService;
    private final UserService userService;
    private final ValoracionService valoracionService; // Servicio de valoraciones

    // Constructor inyectando los 3 servicios
    public AlojamientoController(AlojamientoService_Interfaz alojamientoService, 
                                 UserService userService,
                                 ValoracionService valoracionService) {
        this.alojamientoService = alojamientoService;
        this.userService = userService;
        this.valoracionService = valoracionService;
    }

    /**
     * Muestra la página de resultados de búsqueda (Buscador.html).
     * RECUPERADA la lógica de carga de filtros.
     */
    @GetMapping
    public String mostrarPaginaDeBusqueda(
            @RequestParam(value = "q", required = false) String ciudad,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "people", required = false, defaultValue = "1") int capacity,
            Model model) {

        // 1. Obtenemos el precio máximo para el slider del buscador
        long precioMax = alojamientoService.obtenerPrecioMaximoAlojamientoRedondeado();
        model.addAttribute("precioMaximo", precioMax);

        // 2. Obtenemos los tipos de alojamiento para los checkboxes
        List<String> listaTipos = alojamientoService.obtenerTodosLosTipos();
        model.addAttribute("listaTipos", listaTipos);

        // 3. Pasamos los filtros iniciales de la URL al HTML
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
     * Procesa el guardado del nuevo alojamiento.
     */
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Alojamiento alojamiento, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        
        String userEmail = authentication.getName();
        User anfitrion = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        alojamiento.setAnfitrion(anfitrion);
        alojamientoService.guardar(alojamiento);

        return "redirect:/alojamientos";
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
     * Muestra la página de detalle.
     * INCLUYE la lógica de valoraciones.
     */
    @GetMapping("/detalleAlojamientos")
    public String detalleAlojamientos(@RequestParam Long id, Model model) {
        // 1. Validar ID
        if (id == null) {
            return "redirect:/alojamientos";
        }

        // 2. Buscar alojamiento
        Alojamiento alojamiento = alojamientoService.findById(id);

        if (alojamiento == null) {
            return "redirect:/alojamientos";
        }
        
        // 3. Pasar el alojamiento a la vista
        model.addAttribute("alojamiento", alojamiento);

        // 4. NUEVO: Obtener valoraciones y media (Lógica que pediste hoy)
        List<ValoracionInmueble> valoraciones = valoracionService.obtenerPorAlojamiento(id);
        Double media = valoracionService.obtenerMedia(id);

        model.addAttribute("valoraciones", valoraciones);
        model.addAttribute("mediaValoracion", media);

        return "detalleAlojamientos";
    }
}