package inmobiliaria.es.uclm.negocio.alojamiento;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
// ... (quita el import de List si ya no se usa aquí)

@Controller
@RequestMapping("/alojamientos") // Todas las rutas de vistas de alojamientos
public class AlojamientoController {

    private final AlojamientoService_Interfaz alojamientoService;

    public AlojamientoController(AlojamientoService_Interfaz alojamientoService) {
        this.alojamientoService = alojamientoService;
    }

    // 🔹 Listar en HTML (Thymeleaf)
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("alojamientos", alojamientoService.listarTodos());
        return "alojamientos"; // vista alojamientos.html
    }

    // 🔹 Formulario HTML
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("alojamiento", new Alojamiento());
        return "form-alojamiento";
    }

    // 🔹 Guardar (formulario HTML)
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Alojamiento alojamiento) {
        alojamientoService.guardar(alojamiento);
        return "redirect:/alojamientos";
    }

    // 🔹 Eliminar (formulario HTML)
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        alojamientoService.eliminar(id);
        return "redirect:/alojamientos";
    }

    // 🔹 Página de detalle de alojamiento (justo la que tu JS necesita)
    @GetMapping("/detalleAlojamientos")
    public String detalleAlojamientos() {
        return "detalleAlojamientos"; // templates/detalleAlojamientos.html
    }
}