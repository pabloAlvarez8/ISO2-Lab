package inmobiliaria.es.uclm.negocio.perfil;

import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import inmobiliaria.es.uclm.negocio.alojamiento.AlojamientoService;
import inmobiliaria.es.uclm.negocio.reserva.Reserva;
import inmobiliaria.es.uclm.negocio.reserva.ReservaRepository;
import inmobiliaria.es.uclm.negocio.user.User;
import inmobiliaria.es.uclm.negocio.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.core.Authentication;

import java.security.Principal;
import java.util.List;

@Controller
public class PerfilWebController {

    private final UserService userService;
    private final AlojamientoService alojamientoService;
    private final ReservaRepository reservaRepository;

    public PerfilWebController(UserService userService,
                               AlojamientoService alojamientoService,
                               ReservaRepository reservaRepository) {
        this.userService = userService;
        this.alojamientoService = alojamientoService;
        this.reservaRepository = reservaRepository;
    }

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

    // Muestra la página de aterrizaje "Conviértete en anfitrión" ---
    @GetMapping("/anfitrion")
    public String mostrarPaginaAnfitrion() {
        return "anfitrion"; // Esto busca el archivo anfitrion.html en templates
    }

    @PostMapping("/perfil/convertirse-anfitrion")
    public String convertirseEnAnfitrion(
            Authentication authentication,
            @org.springframework.web.bind.annotation.RequestParam("dni") String dni,
            @org.springframework.web.bind.annotation.RequestParam("telefono") String telefono,
            @org.springframework.web.bind.annotation.RequestParam("iban") String iban) {
        
        // 1. Buscamos quién es el usuario conectado
        String email = authentication.getName();
        User usuario = userService.findByEmail(email).orElse(null);

        if (usuario != null) {
            // 2. Le pasamos todos los datos al servicio
            userService.convertirEnAnfitrion(usuario.getId(), dni, telefono, iban);
        }

        // 3. Recargamos el perfil
        return "redirect:/perfil";
    }
}