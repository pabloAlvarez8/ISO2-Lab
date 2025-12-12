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
    private final PagoService pagoService; // Inyectamos el simulador

    public PagoController(ReservaService reservaService, PagoService pagoService) {
        this.reservaService = reservaService;
        this.pagoService = pagoService;
    }

    // 1. MOSTRAR PANTALLA
    @GetMapping("/pago/{idReserva}")
    public String mostrarPasarela(@PathVariable Long idReserva, Model model) {
        Reserva reserva = reservaService.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        model.addAttribute("reserva", reserva);
        model.addAttribute("precioAPagar", reserva.getPrecioTotal());
        return "pago";
    }

    // 2. PROCESAR PAGO (TARJETA O PAYPAL)
    @PostMapping("/procesar")
    public String procesarPago(
            @RequestParam Long idReserva,
            @RequestParam String metodoPago, // "tarjeta" o "paypal"
            // Datos opcionales según el método
            @RequestParam(required = false) String numeroTarjeta,
            @RequestParam(required = false) String emailPaypal,
            Model model) {

        // Buscar reserva
        Reserva reserva = reservaService.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        boolean pagoExitoso = false;

        // SWITCH PARA ELEGIR LA LÓGICA
        if ("tarjeta".equals(metodoPago)) {
            // Llamamos a tu lógica de tarjeta
            pagoExitoso = pagoService.procesarPagoTarjeta(numeroTarjeta, "12/25", "123");

        } else if ("paypal".equals(metodoPago)) {
            // Llamamos a tu lógica de PayPal
            pagoExitoso = pagoService.procesarPagoPayPal(emailPaypal);
        }

        // RESULTADO
        if (pagoExitoso) {
            reserva.setEstado("PAGADO");
            reservaService.guardar(reserva);
            return "redirect:/alojamientos"; // O a "Mis Viajes"
        } else {
            // Si falla, volvemos a la pasarela con un error
            return "redirect:/pagos/pasarela/" + idReserva + "?error=true";
        }
    }
}