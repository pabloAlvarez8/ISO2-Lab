package inmobiliaria.es.uclm.negocio.reserva;

import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import inmobiliaria.es.uclm.negocio.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    @Test
    @DisplayName("Should create a reservation and set all fields correctly")
    void testCreateReservation() {
        // 1. Given (Preparar datos)
        Reserva reservation = new Reserva();

        User tenant = new User();
        tenant.setId(1L);
        tenant.setEmail("tenant@example.com");

        Alojamiento property = new Alojamiento();
        property.setId(100L);
        property.setNombre("Beach House");

        LocalDate checkIn = LocalDate.of(2025, 6, 1);
        LocalDate checkOut = LocalDate.of(2025, 6, 10);

        // 2. When (Ejecutar acciones - Setters)
        reservation.setId(50L);
        reservation.setInquilino(tenant);
        reservation.setAlojamiento(property);
        reservation.setFechaEntrada(checkIn);
        reservation.setFechaSalida(checkOut);
        reservation.setPrecioTotal(1200.50);
        reservation.setEstado("CONFIRMED");

        // Probamos el método manual específico para 'aceptada'
        reservation.setAceptada(true);

        // 3. Then (Verificar - Getters)
        assertAll("Verify reservation properties",
                () -> assertEquals(50L, reservation.getId()),
                () -> assertEquals(tenant, reservation.getInquilino()),
                () -> assertEquals(property, reservation.getAlojamiento()),
                () -> assertEquals(checkIn, reservation.getFechaEntrada()),
                () -> assertEquals(checkOut, reservation.getFechaSalida()),
                () -> assertEquals(1200.50, reservation.getPrecioTotal(), 0.001),
                () -> assertEquals("CONFIRMED", reservation.getEstado()),
                () -> assertTrue(reservation.getAceptada(), "The manually coded getter for 'aceptada' should return true")
        );
    }

    @Test
    @DisplayName("Should handle the 'aceptada' boolean field correctly")
    void testAcceptedField() {
        Reserva reservation = new Reserva();

        // Por defecto debe ser null (es un Boolean objeto, no boolean primitivo)
        assertNull(reservation.getAceptada());

        reservation.setAceptada(false);
        assertFalse(reservation.getAceptada());
    }
}