package inmobiliaria.es.uclm.negocio.perfil;

import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import inmobiliaria.es.uclm.negocio.alojamiento.AlojamientoService_Interfaz;
import inmobiliaria.es.uclm.negocio.reserva.Reserva;
import inmobiliaria.es.uclm.negocio.reserva.ReservaRepository;
import inmobiliaria.es.uclm.negocio.user.User;
import inmobiliaria.es.uclm.negocio.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class PerfilWebController {

    @Autowired private UserService userService;
    @Autowired private AlojamientoService_Interfaz alojamientoService;
    @Autowired private ReservaRepository reservaRepository; // Inyectamos el repo de reservas

    @GetMapping("/perfil")
    public String verPerfil(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User usuario = userService.findByEmail(principal.getName()).orElse(null);
        if (usuario == null) return "redirect:/login";

        model.addAttribute("usuario", usuario);

        // --- DATOS PARA MODO ANFITRION ---
        // 1. Alojamientos que yo he subido
        List<Alojamiento> misAlojamientos = alojamientoService.listarAlojamientosDeAnfitrion(usuario.getId());
        model.addAttribute("misAlojamientos", misAlojamientos);

        // 2. Solicitudes de gente que quiere ir a mis casas
        List<Reserva> solicitudesRecibidas = reservaRepository.findByAlojamiento_Anfitrion_Id(usuario.getId());
        model.addAttribute("solicitudesRecibidas", solicitudesRecibidas);

        // --- DATOS PARA MODO INQUILINO ---
        // 3. Reservas que yo he hecho para ir a otros sitios
        List<Reserva> misReservas = reservaRepository.findByInquilino_Id(usuario.getId());
        model.addAttribute("misReservas", misReservas);

        return "perfil";
    }
}