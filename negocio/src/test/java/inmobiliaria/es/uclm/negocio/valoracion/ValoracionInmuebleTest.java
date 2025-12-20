package inmobiliaria.es.uclm.negocio.valoracion;

import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import inmobiliaria.es.uclm.negocio.user.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValoracionInmuebleTest {

    @Test
    void testConstructorYGetters() {
        // GIVEN
        Alojamiento aloj = new Alojamiento();
        aloj.setId(1L);
        
        User user = new User();
        user.setId(2L);
        
        // WHEN
        ValoracionInmueble val = new ValoracionInmueble(aloj, user, 4.5, "Muy limpio");

        // THEN
        assertEquals(1L, val.getInmueble().getId());
        assertEquals(2L, val.getUsuario().getId());
        assertEquals(4.5, val.getPuntuacion());
        assertEquals("Muy limpio", val.getComentario());
        assertNull(val.getId()); // El ID es null hasta que se guarda en BD
    }

    @Test
    void testSetters() {
        ValoracionInmueble val = new ValoracionInmueble();
        val.setPuntuacion(1.0);
        
        assertEquals(1.0, val.getPuntuacion());
    }
}