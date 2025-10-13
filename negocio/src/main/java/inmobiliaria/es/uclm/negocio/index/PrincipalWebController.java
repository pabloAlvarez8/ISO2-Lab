package inmobiliaria.es.uclm.negocio.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrincipalWebController {

    @GetMapping("/")
    public String redirectToIndex() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index() {
        return "Index";
    }

    @GetMapping("/buscador")
    public String buscador() {
        return "Buscador";
    }
}
