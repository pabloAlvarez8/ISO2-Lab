// Archivo: .../negocio/index/PrincipalWebController.java
package inmobiliaria.es.uclm.negocio.index;

import inmobiliaria.es.uclm.negocio.alojamiento.AlojamientoService_Interfaz;
import inmobiliaria.es.uclm.negocio.alojamiento.dto.DestinoDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PrincipalWebController {

    private final AlojamientoService_Interfaz alojamientoService;

    public PrincipalWebController(AlojamientoService_Interfaz alojamientoService) {
        this.alojamientoService = alojamientoService;
    }

    @GetMapping("/index") // (1) Escucha en la raíz de tu web
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
}

