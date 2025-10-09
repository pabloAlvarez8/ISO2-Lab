package inmobiliaria.es.uclm.negocio.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrincipalWebController {

    @GetMapping("/inicio")
    public String mostrarPaginaPrincipal() {
        return "paginaPrincipal"; // Carga index.html desde templates
    }
}