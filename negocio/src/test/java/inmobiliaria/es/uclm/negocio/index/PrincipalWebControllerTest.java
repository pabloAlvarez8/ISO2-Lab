package inmobiliaria.es.uclm.negocio.index;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
// Importamos el procesador para inyectar el usuario personalizado
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.math.BigDecimal;
import java.util.Collections;
// (Hemos quitado el import de Optional porque no hace falta)

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import inmobiliaria.es.uclm.negocio.alojamiento.AlojamientoService;

// --- IMPORTANTE: Si 'Reserva' está en otro paquete, IntelliJ te pedirá importarla (Alt+Enter) ---
import inmobiliaria.es.uclm.negocio.reserva.Reserva;
// ---------------------------------------------------------------------------------------------

@SpringBootTest
@AutoConfigureMockMvc
public class PrincipalWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlojamientoService alojamientoService;

    // Clase auxiliar para simular un usuario con ID (para que Thymeleaf no falle)
    // Puedes ignorar los avisos de "unused" o "Lombok", está correcto.
    static class UsuarioConId extends org.springframework.security.core.userdetails.User {
        private final Long id;
        public UsuarioConId(String username, String password, Long id) {
            super(username, password, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
            this.id = id;
        }
        public Long getId() { return id; }
    }

    @Test
    public void testDetalleAlojamientos_DeberiaRetornarVistaDetalle() throws Exception {
        // 1. Datos
        Alojamiento casaFalsa = new Alojamiento();
        casaFalsa.setId(1L);
        casaFalsa.setNombre("Casa de Prueba");
        casaFalsa.setDescripcion("Una casa muy bonita");
        casaFalsa.setPrecio(BigDecimal.valueOf(150.0));
        casaFalsa.setFotoUrl("https://via.placeholder.com/150");

        // 2. Mock (CORREGIDO: Devolvemos el objeto directo, SIN Optional)
        when(alojamientoService.findById(1L)).thenReturn(casaFalsa);

        // 3. Usuario Falso
        UsuarioConId usuarioFalso = new UsuarioConId("pepe", "pass", 99L);

        // 4. Ejecución
        mockMvc.perform(get("/detalleAlojamientos.html")
                        .param("id", "1")
                        .with(user(usuarioFalso)) // Inyectamos usuario
                        .flashAttr("alojamiento", casaFalsa))
                .andExpect(status().isOk())
                .andExpect(view().name("detalleAlojamientos"))
                .andExpect(model().attributeExists("alojamiento"));
    }

    @Test
    public void testPago_DeberiaRetornarVistaPago() throws Exception {
        // 1. Datos
        Alojamiento casaFalsa = new Alojamiento();
        casaFalsa.setId(1L);
        casaFalsa.setNombre("Casa para Pagar");
        casaFalsa.setPrecio(BigDecimal.valueOf(200.0));

        // 2. Mock (CORREGIDO: Sin Optional)
        when(alojamientoService.findById(anyLong())).thenReturn(casaFalsa);

        // 3. Crear Reserva (Obligatorio para pago.html)
        Reserva reservaFalsa = new Reserva();
        reservaFalsa.setAlojamiento(casaFalsa);

        // 4. Usuario Falso
        UsuarioConId usuarioFalso = new UsuarioConId("pepe", "pass", 99L);

        mockMvc.perform(get("/pago")
                        .with(user(usuarioFalso))
                        .flashAttr("reserva", reservaFalsa)) // Pasamos la reserva
                .andExpect(status().isOk())
                .andExpect(view().name("pago"));
    }
}