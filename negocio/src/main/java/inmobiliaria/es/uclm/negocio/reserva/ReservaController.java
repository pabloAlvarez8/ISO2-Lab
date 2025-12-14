package inmobiliaria.es.uclm.negocio.reserva;

import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import inmobiliaria.es.uclm.negocio.alojamiento.AlojamientoService_Interfaz;
import inmobiliaria.es.uclm.negocio.user.User;
import inmobiliaria.es.uclm.negocio.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService; // Asumo que tienes este servicio
    private final AlojamientoService_Interfaz alojamientoService;
    private final UserService userService;
    private final ReservaRepository reservaRepository;

    // Constructor para inyección de dependencias
    public ReservaController(ReservaService reservaService,
                             AlojamientoService_Interfaz alojamientoService,
                             UserService userService,
                             ReservaRepository reservaRepository) {
        this.reservaService = reservaService;
        this.alojamientoService = alojamientoService;
        this.userService = userService;
        this.reservaRepository = reservaRepository;
    }

    // --- API para el Calendario (Devuelve fechas ocupadas) ---
    @GetMapping("/api/ocupadas/{idAlojamiento}")
    @ResponseBody
    public List<String> getFechasOcupadas(@PathVariable Long idAlojamiento) {
        return reservaRepository.findReservasFuturas(idAlojamiento).stream()
                .map(r -> r.getFechaEntrada().toString() + ":" + r.getFechaSalida().toString())
                .collect(Collectors.toList());
    }

    @PostMapping("/crear")
    public String crearReserva(
            @RequestParam Long alojamientoId,
            @RequestParam String fechaEntrada,
            @RequestParam String fechaSalida,
            Authentication authentication,
            RedirectAttributes redirectAttrs) {

        // 1. Obtener quién es el usuario y qué casa quiere
        String emailUsuario = authentication.getName();
        User inquilino = userService.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Alojamiento alojamiento = alojamientoService.findById(alojamientoId);
        // Nota: Asegúrate de tener un método findById en tu servicio de alojamiento

        // 2. Convertir las fechas (Vienen como String "2023-12-25")
        LocalDate entrada = LocalDate.parse(fechaEntrada);
        LocalDate salida = LocalDate.parse(fechaSalida);

        // 2. Validar fechas lógicas
        if (entrada.isAfter(salida) || entrada.isBefore(LocalDate.now())) {
            redirectAttrs.addFlashAttribute("error", "Fechas no válidas.");
            return "redirect:/alojamientos/detalle/" + alojamientoId;
        }

        // 3. Validar disponibilidad en base de datos
        if (reservaRepository.countSolapamientos(alojamientoId, entrada, salida) > 0) {
            redirectAttrs.addFlashAttribute("error", "¡Lo sentimos! Esas fechas ya están ocupadas.");
            return "redirect:/alojamientos/detalle/" + alojamientoId;
        }

        // 3. LÓGICA DE PRECIO (La parte importante)
        long dias = ChronoUnit.DAYS.between(entrada, salida);


        if (dias < 1) {
            // Manejo básico de errores si las fechas están mal
            return "redirect:/alojamientos/detalleAlojamientos?error=fechas";
        }

        BigDecimal precioDia = alojamiento.getPrecio();
        BigDecimal diasBD = new BigDecimal(dias);
        // Calculamos el total usando el precio REAL de la base de datos
        double precioTotal = precioDia.multiply(diasBD).doubleValue();

        // 4. Crear y guardar la Reserva "PENDIENTE DE PAGO"
        Reserva reserva = new Reserva();
        reserva.setInquilino(inquilino);
        reserva.setAlojamiento(alojamiento);
        reserva.setFechaEntrada(entrada);
        reserva.setFechaSalida(salida);
        reserva.setPrecioTotal(precioTotal); // Guardamos el precio calculado
        reserva.setEstado("PENDIENTE_PAGO");

        Reserva reservaGuardada = reservaService.guardar(reserva);

        // 5. REDIRECCIÓN MÁGICA
        // En lugar de ir a una página de "gracias", lo enviamos a PAGAR esa reserva específica
        return "redirect:/pagos/pago/" + reservaGuardada.getId();
    }
}