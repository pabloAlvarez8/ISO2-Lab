package inmobiliaria.es.uclm.negocio.alojamiento;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/alojamientos")
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

    // 🔹 Listar en JSON (para fetch desde frontend)
    @GetMapping("/api")
    @ResponseBody
    public List<Alojamiento> listarApi() {
        return alojamientoService.listarTodos();
    }

    // 🔹 Buscar por ciudad en JSON
    @GetMapping("/api/buscar")
    @ResponseBody
    public List<Alojamiento> buscarPorCiudadApi(@RequestParam String ciudad) {
        return alojamientoService.buscarPorCiudad(ciudad);
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

    // 🔹 Página de detalle de alojamiento
    @GetMapping("/detalleAlojamientos")
    public String detalleAlojamientos() {
        return "detalleAlojamientos"; // templates/detalleAlojamientos.html
    }
    
    
    
}
