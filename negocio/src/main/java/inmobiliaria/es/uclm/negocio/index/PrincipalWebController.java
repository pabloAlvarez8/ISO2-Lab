package inmobiliaria.es.uclm.negocio.index;

import inmobiliaria.es.uclm.negocio.alojamiento.AlojamientoService;
import inmobiliaria.es.uclm.negocio.alojamiento.dto.DestinoDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class PrincipalWebController {

    private final AlojamientoService alojamientoService;

    public PrincipalWebController(AlojamientoService alojamientoService) {
        this.alojamientoService = alojamientoService;
    }

    @GetMapping({ "/", "/index" }) // (1) Escucha en la raíz de tu web
    public String paginaDeInicio(Model model) {

        // (2) Llama al servicio para obtener los destinos
        List<DestinoDTO> destinos = alojamientoService.obtenerDestinosPopulares();

        // (3) Añade la lista al Model para que Thymeleaf la pueda usar
        model.addAttribute("destinosPopulares", destinos);

        // (4.b) Devuelve el nombre de la plantilla HTML
        return "index"; // (Asegúrate que tu HTML se llama 'index.html')
    }

    @GetMapping("/buscador")
    public String buscador() {
        return "Buscador";
    }

    @GetMapping("/pago")
    public String mostrarPaginaPago() {
        return "pago"; // <-- nombre del archivo en templates sin .html
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(HttpSession session) {
        // 1. Recuperar la URL guardada en la sesión (guardada por LoginWebController)
        String urlPrevio = (String) session.getAttribute("urlPrevio");

        // 2. Comprobar si existe y redirigir
        if (urlPrevio != null && !urlPrevio.isEmpty()) {
            // "redirect:" le dice al navegador que vaya a esa dirección (Código 302)
            return "redirect:" + urlPrevio;
        } else {
            // Si no hay historial, mandamos al usuario al Index
            return "redirect:/";
        }
    }

    @GetMapping("/detalleAlojamientos.html")
    public String mostrarDetalle() {
    return "detalleAlojamientos";
    }
}
