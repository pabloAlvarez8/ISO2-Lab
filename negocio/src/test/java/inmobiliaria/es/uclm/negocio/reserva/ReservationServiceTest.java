package inmobiliaria.es.uclm.negocio.reserva;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Habilita Mockito en JUnit 5
class ReservationServiceTest {

    @Mock
    private ReservaRepository reservaRepository; // El "falso" repositorio

    @InjectMocks
    private ReservaService reservaService; // El servicio real con el mock inyectado dentro

    @Test
    @DisplayName("Should save a reservation correctly")
    void testSaveReservation() {
        // 1. Given
        Reserva reservationToSave = new Reserva();
        reservationToSave.setEstado("PENDIENTE");

        // Cuando al repo le digan 'save', devuelve el mismo objeto
        when(reservaRepository.save(reservationToSave)).thenReturn(reservationToSave);

        // 2. When
        Reserva savedReservation = reservaService.guardar(reservationToSave);

        // 3. Then
        assertNotNull(savedReservation);
        assertEquals("PENDIENTE", savedReservation.getEstado());
        verify(reservaRepository).save(reservationToSave); // Verifica que se llamó al repo
    }

    @Test
    @DisplayName("Should return a reservation when found by ID")
    void testFindById_Found() {
        // 1. Given
        Long id = 1L;
        Reserva mockReserva = new Reserva();
        mockReserva.setId(id);

        when(reservaRepository.findById(id)).thenReturn(Optional.of(mockReserva));

        // 2. When
        Reserva result = reservaService.findById(id);

        // 3. Then
        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    @DisplayName("Should return null when reservation ID does not exist")
    void testFindById_NotFound() {
        // 1. Given
        Long id = 99L;
        when(reservaRepository.findById(id)).thenReturn(Optional.empty());

        // 2. When
        Reserva result = reservaService.findById(id);

        // 3. Then
        assertNull(result, "Service should return null if repo returns empty Optional");
    }

    @Test
    @DisplayName("Should delete reservation by ID")
    void testDeleteReservation() {
        // 1. Given
        Long id = 5L;

        // 2. When
        reservaService.eliminar(id);

        // 3. Then
        // Verificamos que el servicio llamó al método deleteById del repositorio con el ID 5
        verify(reservaRepository).deleteById(id);
    }

    @Test
    @DisplayName("Should return list of reservations for a specific user (tenant)")
    void testGetReservationsByUser() {
        // 1. Given
        Long userId = 10L;
        Reserva r1 = new Reserva();
        Reserva r2 = new Reserva();
        List<Reserva> mockList = Arrays.asList(r1, r2);

        when(reservaRepository.findByInquilino_Id(userId)).thenReturn(mockList);

        // 2. When
        List<Reserva> result = reservaService.obtenerReservasDeUsuario(userId);

        // 3. Then
        assertEquals(2, result.size());
        verify(reservaRepository).findByInquilino_Id(userId);
    }

    @Test
    @DisplayName("Should return list of reservations for a specific host")
    void testGetReservationsByHost() {
        // 1. Given
        Long hostId = 20L;
        List<Reserva> mockList = Arrays.asList(new Reserva()); // Lista con 1 elemento

        when(reservaRepository.findByAlojamiento_Anfitrion_Id(hostId)).thenReturn(mockList);

        // 2. When
        List<Reserva> result = reservaService.obtenerReservasDeAnfitrion(hostId);

        // 3. Then
        assertEquals(1, result.size());
        verify(reservaRepository).findByAlojamiento_Anfitrion_Id(hostId);
    }
}