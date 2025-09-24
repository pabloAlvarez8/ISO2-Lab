package alquiler.negocio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import alquiler.negocio.service.ViviendaService;
@Controller
public class ViviendaController {

    private final ViviendaService service;

    public ViviendaController(ViviendaService service) {
        this.service = service;
    }

    @GetMapping("/viviendas")
    public String listarViviendas(Model model) {
        model.addAttribute("viviendas", service.listarViviendas());
        return "viviendas";
    }

    @GetMapping("/vivienda/{id}")
    public String detalleVivienda(@PathVariable Long id, Model model) {
        model.addAttribute("vivienda", service.obtenerVivienda(id));
        return "detalle";
    }

    @PostMapping("/vivienda/{id}/alquilar")
    public String alquilarVivienda(@PathVariable Long id) {
        service.alquilarVivienda(id);
        return "redirect:/viviendas";
    }
}
