package inmobiliaria.es.uclm.negocio.pago;

import inmobiliaria.es.uclm.negocio.reserva.Reserva;
import inmobiliaria.es.uclm.negocio.reserva.ReservaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pagos")
public class PagoController {

    private final ReservaService reservaService;
    private final PagoService pagoService;

    public PagoController(ReservaService reservaService, PagoService pagoService) {
        this.reservaService = reservaService;
        this.pagoService = pagoService;
    }

    // 1. MOSTRAR PANTALLA
    @GetMapping("/pago/{idReserva}")
    public String mostrarPasarela(@PathVariable Long idReserva, Model model) {
        // CORRECCIÓN: Ya no usamos .orElseThrow() porque findById devuelve la Reserva directa
        Reserva reserva = reservaService.findById(idReserva);

        // Comprobación manual: Si es null, lanzamos el error
        if (reserva == null) {
            throw new RuntimeException("Reserva no encontrada");
        }

        model.addAttribute("reserva", reserva);
        model.addAttribute("precioAPagar", reserva.getPrecioTotal());
        return "pago";
    }

    // 2. PROCESAR PAGO (TARJETA O PAYPAL)
    @PostMapping("/procesar")
    public String procesarPago(
            @RequestParam Long idReserva,
            @RequestParam String metodoPago, 
            @RequestParam(required = false) String numeroTarjeta,
            @RequestParam(required = false) String emailPaypal,
            Model model) {

        // CORRECCIÓN: Igual aquí, quitamos .orElseThrow()
        Reserva reserva = reservaService.findById(idReserva);

        if (reserva == null) {
            throw new RuntimeException("Reserva no encontrada");
        }

        boolean pagoExitoso = false;

        // Lógica de pago
        if ("tarjeta".equals(metodoPago)) {
            pagoExitoso = pagoService.procesarPagoTarjeta(numeroTarjeta, "12/25", "123");
        } else if ("paypal".equals(metodoPago)) {
            pagoExitoso = pagoService.procesarPagoPayPal(emailPaypal);
        }

        // RESULTADO
        if (pagoExitoso) {
            reserva.setEstado("PAGADO");
            reservaService.guardar(reserva);
            // Redirigimos al perfil para que veas la reserva actualizada
            return "redirect:/perfil"; 
        } else {
            return "redirect:/pagos/pago/" + idReserva + "?error=true";
        }
    }
}