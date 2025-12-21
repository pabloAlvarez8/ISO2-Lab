package inmobiliaria.es.uclm.negocio.reserva;

import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import inmobiliaria.es.uclm.negocio.alojamiento.AlojamientoService;
import inmobiliaria.es.uclm.negocio.user.User;
import inmobiliaria.es.uclm.negocio.user.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller; // USAR ESTE
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

import java.util.Optional;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final AlojamientoService alojamientoService;
    private final UserService userService;
    private final ReservaRepository reservaRepository;

    public ReservaController(ReservaService reservaService,
                             AlojamientoService alojamientoService,
                             UserService userService,
                             ReservaRepository reservaRepository) {
        this.reservaService = reservaService;
        this.alojamientoService = alojamientoService;
        this.userService = userService;
        this.reservaRepository = reservaRepository;
    }

    // --- API para el Calendario ---
    // AÑADIDO: @ResponseBody. Esto le dice a Spring: "Este método específico SÍ es REST (JSON), no busques una vista HTML".
    @GetMapping("/api/ocupadas/{idAlojamiento}")
    @ResponseBody
    public List<String> getFechasOcupadas(@PathVariable Long idAlojamiento) {
        return reservaRepository.findReservasFuturas(idAlojamiento).stream()
                .map(r -> r.getFechaEntrada().toString() + ":" + r.getFechaSalida().toString())
                .collect(Collectors.toList());
    }

    @GetMapping("/booking-detail/{id}") 
    public String verDetalleReserva(@PathVariable Long id, Model model, Authentication authentication) { 
        Optional<Reserva> reservaOpt = reservaRepository.findById(id);

        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();

            String emailUsuarioActual = authentication.getName();
            String emailInquilino = reserva.getInquilino().getEmail();
            String emailAnfitrion = reserva.getAlojamiento().getAnfitrion().getEmail();

            if (!emailUsuarioActual.equals(emailInquilino) && !emailUsuarioActual.equals(emailAnfitrion)) {
                return "redirect:/perfil?error=NoTienesPermiso";
            }

            model.addAttribute("reserva", reserva);

            long dias = java.time.temporal.ChronoUnit.DAYS.between(reserva.getFechaEntrada(), reserva.getFechaSalida());
            model.addAttribute("diasEstancia", dias);

            return "booking-details";
        } else {
            return "redirect:/perfil?error=ReservaNoEncontrada";
        }
    }

    @PostMapping("/crear")
    public String crearReserva(
            @RequestParam Long alojamientoId,
            @RequestParam String fechaEntrada,
            @RequestParam String fechaSalida,
            Authentication authentication,
            RedirectAttributes redirectAttrs) {

        String emailUsuario = authentication.getName();
        User inquilino = userService.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Alojamiento alojamiento = alojamientoService.findById(alojamientoId);

        LocalDate entrada = LocalDate.parse(fechaEntrada);
        LocalDate salida = LocalDate.parse(fechaSalida);

        if (entrada.isAfter(salida) || entrada.isBefore(LocalDate.now())) {
            redirectAttrs.addFlashAttribute("error", "Fechas no válidas.");
            return "redirect:/alojamientos/detalle/" + alojamientoId;
        }

        if (reservaRepository.countSolapamientos(alojamientoId, entrada, salida) > 0) {
            redirectAttrs.addFlashAttribute("error", "¡Lo sentimos! Esas fechas ya están ocupadas.");
            return "redirect:/alojamientos/detalle/" + alojamientoId;
        }

        long dias = ChronoUnit.DAYS.between(entrada, salida);

        if (dias < 1) {
            return "redirect:/alojamientos/detalleAlojamientos?error=fechas";
        }

        BigDecimal precioDia = alojamiento.getPrecio();
        BigDecimal diasBD = new BigDecimal(dias);
        double precioTotal = precioDia.multiply(diasBD).doubleValue();

        Reserva reserva = new Reserva();
        reserva.setInquilino(inquilino);
        reserva.setAlojamiento(alojamiento);
        reserva.setFechaEntrada(entrada);
        reserva.setFechaSalida(salida);
        reserva.setPrecioTotal(precioTotal);
        reserva.setEstado("PENDIENTE_PAGO");

        Reserva reservaGuardada = reservaService.guardar(reserva);

        return "redirect:/pagos/pago/" + reservaGuardada.getId();
    }

    @GetMapping("/aceptar/{id}")
    public String aceptarReserva(@PathVariable Long id) {
        Reserva reserva = reservaService.findById(id);
        if (reserva != null) {
            reserva.setAceptada(true);
            reservaService.guardar(reserva);
        }
        return "redirect:/perfil";
    }

    @GetMapping("/rechazar/{id}")
    public String rechazarReserva(@PathVariable Long id) {
        Reserva reserva = reservaService.findById(id);
        if (reserva != null) {
            reserva.setAceptada(false);
            reservaService.guardar(reserva);
        }
        return "redirect:/perfil";
    }
}