package inmobiliaria.es.uclm.negocio.alojamiento;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import inmobiliaria.es.uclm.negocio.valoracion.ValoracionService;
import inmobiliaria.es.uclm.negocio.valoracion.ValoracionInmueble;
import inmobiliaria.es.uclm.negocio.user.UserService;

import java.util.List;

@Controller
@RequestMapping("/alojamientos")
public class AlojamientoController {

    private final AlojamientoService alojamientoService;
    private final UserService userService;
    private final ValoracionService valoracionService;

    public AlojamientoController(AlojamientoService alojamientoService,
                                 UserService userService,
                                 ValoracionService valoracionService) {
        this.alojamientoService = alojamientoService;
        this.userService = userService;
        this.valoracionService = valoracionService;
    }

    /**
     * Muestra la página de resultados de búsqueda (Buscador.html).
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
        return "Buscador";
    }

    // Muestra el formulario
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("alojamiento", new Alojamiento());
        return "nuevoAlojamiento";
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

        // Delegamos TODA la lógica al servicio (buscar usuario, asignar casa, cambiar
        // rol)
        alojamientoService.guardarNuevoAlojamiento(alojamiento, userEmail);

        // Al terminar, volvemos al perfil para ver la nueva casa en la lista
        return "redirect:/perfil";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        alojamientoService.eliminar(id);
        return "redirect:/perfil"; // Mejor volver al perfil si borras desde ahí
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

        // 4. NUEVO: Obtener valoraciones y media
        List<ValoracionInmueble> valoraciones = valoracionService.obtenerPorAlojamiento(id);
        Double media = valoracionService.obtenerMedia(id);

        model.addAttribute("valoraciones", valoraciones);
        model.addAttribute("mediaValoracion", media);

        return "detalleAlojamientos";
    }

    // EDITAR ALOJAMIENTO
    @GetMapping("/editar/{id}")
    public String editarAlojamiento(@PathVariable Long id, Model model) {
        // 1. Buscamos el alojamiento por su ID
        Alojamiento alojamiento = alojamientoService.findById(id);

        // 2. Si existe, lo pasamos al modelo y abrimos el formulario
        if (alojamiento != null) {
            model.addAttribute("alojamiento", alojamiento);
            return "nuevoAlojamiento"; // Reutilizamos la vista de crear
        }

        return "redirect:/perfil";
    }
}