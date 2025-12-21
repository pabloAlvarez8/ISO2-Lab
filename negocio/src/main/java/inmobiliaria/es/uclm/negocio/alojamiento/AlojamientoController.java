package inmobiliaria.es.uclm.negocio.alojamiento;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.format.annotation.DateTimeFormat; // IMPORTANTE: Necesario para recibir fechas

import inmobiliaria.es.uclm.negocio.valoracion.ValoracionService;
import inmobiliaria.es.uclm.negocio.valoracion.ValoracionInmueble;

import java.time.LocalDate; // IMPORTANTE
import java.util.List;

@Controller
@RequestMapping("/alojamientos")
public class AlojamientoController {

    private static final String ATTR_ALOJAMIENTO = "alojamiento";
    private static final String REDIRECT_PERFIL = "redirect:/perfil";

    private final AlojamientoService alojamientoService;
    private final ValoracionService valoracionService;

    public AlojamientoController(AlojamientoService alojamientoService,
                                 ValoracionService valoracionService) {
        this.alojamientoService = alojamientoService;
        this.valoracionService = valoracionService;
    }

    /**
     * Muestra la página de resultados de búsqueda (Buscador.html).
     * ACTUALIZADO: Ahora recibe y pasa las fechas al modelo.
     */
    @GetMapping
    public String mostrarPaginaDeBusqueda(
            @RequestParam(value = "q", required = false) String ciudad,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "people", required = false, defaultValue = "1") int capacity,

            // --- NUEVOS PARÁMETROS DE FECHA ---
            @RequestParam(value = "checkin", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkin,

            @RequestParam(value = "checkout", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkout,
            // ----------------------------------

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

        // --- PASAMOS LAS FECHAS AL MODELO ---
        model.addAttribute("filtroCheckin", checkin);
        model.addAttribute("filtroCheckout", checkout);
        // ------------------------------------

        return "Buscador";
    }

    // Muestra el formulario
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute(ATTR_ALOJAMIENTO, new Alojamiento());
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

        // Delegamos TODA la lógica al servicio
        alojamientoService.guardarNuevoAlojamiento(alojamiento, userEmail);

        return REDIRECT_PERFIL;
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        alojamientoService.eliminar(id);
        return REDIRECT_PERFIL;
    }

    /**
     * Muestra la página de detalle.
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
        model.addAttribute(ATTR_ALOJAMIENTO, alojamiento);

        // 4. Obtener valoraciones y media
        List<ValoracionInmueble> valoraciones = valoracionService.obtenerPorAlojamiento(id);
        Double media = valoracionService.obtenerMedia(id);

        model.addAttribute("valoraciones", valoraciones);
        model.addAttribute("mediaValoracion", media);

        return "detalleAlojamientos";
    }

    // EDITAR ALOJAMIENTO
    @GetMapping("/editar/{id}")
    public String editarAlojamiento(@PathVariable Long id, Model model) {
        Alojamiento alojamiento = alojamientoService.findById(id);

        if (alojamiento != null) {
            model.addAttribute(ATTR_ALOJAMIENTO, alojamiento);
            return "nuevoAlojamiento";
        }

        return REDIRECT_PERFIL;
    }
}