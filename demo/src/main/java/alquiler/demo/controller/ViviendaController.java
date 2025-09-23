package alquiler.demo.controller;

import alquiler.demo.entity.Vivienda;
import alquiler.demo.service.ViviendaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/viviendas")
public class ViviendaController {

    private final ViviendaService viviendaService;

    public ViviendaController(ViviendaService viviendaService) {
        this.viviendaService = viviendaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("viviendas", viviendaService.listarTodas());
        return "index"; // plantilla index.html
    }

    @GetMapping("/nueva")
    public String mostrarFormulario(Model model) {
        model.addAttribute("vivienda", new Vivienda());
        return "form";
    }

    @PostMapping
    public String guardar(@ModelAttribute Vivienda vivienda) {
        viviendaService.guardar(vivienda);
        return "redirect:/viviendas";
    }
}
