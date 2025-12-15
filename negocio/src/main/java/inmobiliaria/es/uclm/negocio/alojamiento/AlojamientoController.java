package inmobiliaria.es.uclm.negocio.alojamiento;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/alojamientos")
public class AlojamientoController {

    private final AlojamientoService_Interfaz alojamientoService;
    // No necesitamos UserService aquí porque lo gestionaremos dentro del AlojamientoService

    public AlojamientoController(AlojamientoService_Interfaz alojamientoService) {
        this.alojamientoService = alojamientoService;
    }

    // --- MÉTODOS DE BÚSQUEDA (Los dejamos igual) ---
    @GetMapping
    public String mostrarPaginaDeBusqueda(
            @RequestParam(value = "q", required = false) String ciudad,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "people", required = false, defaultValue = "1") int capacity,
            Model model) {

        model.addAttribute("filtroCiudad", ciudad);
        model.addAttribute("filtroTipo", type);
        model.addAttribute("filtroCapacidad", capacity);
        return "Buscador";
    }

    // --- NUEVO: Muestra el formulario ---
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("alojamiento", new Alojamiento());
        // IMPORTANTE: Aquí debe ir el nombre exacto de tu archivo HTML creado en templates
        return "nuevoAlojamiento"; 
    }

    // --- NUEVO: Guarda el alojamiento ---
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Alojamiento alojamiento, Authentication authentication) {
        // Obtenemos el email del usuario conectado
        String userEmail = authentication.getName();
        
        // Delegamos TODA la lógica al servicio (buscar usuario, asignar casa, cambiar rol)
        alojamientoService.guardarNuevoAlojamiento(alojamiento, userEmail);

        // Al terminar, volvemos al perfil para ver la nueva casa en la lista
        return "redirect:/perfil"; 
    }

    // --- ELIMINAR ---
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        alojamientoService.eliminar(id);
        return "redirect:/perfil"; // Mejor volver al perfil si borras desde ahí
    }

    // --- DETALLE ---
    @GetMapping("/detalleAlojamientos")
    public String detalleAlojamientos(@RequestParam Long id, Model model) {
        if (id == null) return "redirect:/alojamientos";
        Alojamiento alojamiento = alojamientoService.findById(id);
        if (alojamiento == null) return "redirect:/alojamientos";
        
        model.addAttribute("alojamiento", alojamiento);
        return "detalleAlojamientos";
    }

    // --- EDITAR ALOJAMIENTO ---
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