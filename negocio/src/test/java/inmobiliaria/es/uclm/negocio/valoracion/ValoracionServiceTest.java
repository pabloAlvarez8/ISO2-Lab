package inmobiliaria.es.uclm.negocio.valoracion;

import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import inmobiliaria.es.uclm.negocio.alojamiento.AlojamientoRepository;
import inmobiliaria.es.uclm.negocio.reserva.ReservaRepository;
import inmobiliaria.es.uclm.negocio.user.User;
import inmobiliaria.es.uclm.negocio.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValoracionServiceTest {

    @Mock private ValoracionRepository valoracionRepo;
    @Mock private AlojamientoRepository alojamientoRepo;
    @Mock private UserRepository userRepo;
    @Mock private ReservaRepository reservaRepo;

    @InjectMocks
    private ValoracionService valoracionService;

    private User usuario;
    private Alojamiento alojamiento;

    @BeforeEach
    void setUp() {
        usuario = new User();
        usuario.setId(1L);
        usuario.setEmail("test@test.com");

        alojamiento = new Alojamiento();
        alojamiento.setId(10L);
        alojamiento.setNombre("Casa de Prueba");
        // No hace falta setear precio/BigDecimal aquí porque el servicio no lo usa para valorar
    }

    // --------------------------------------------------------
    // Tests: GUARDAR VALORACIÓN
    // --------------------------------------------------------

    @Test
    void guardarValoracion_UsuarioNoHaVisitado_DeberiaLanzarExcepcion() {
        // GIVEN: El repo de reservas dice FALSE
        when(reservaRepo.haEmpezadoEstancia(eq(1L), eq(10L), any(LocalDate.class)))
                .thenReturn(false);

        // WHEN & THEN: Esperamos el error
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            valoracionService.guardarValoracion(10L, 1L, 5.0, "Genial")
        );

        assertEquals("Para poder escribir una reseña de este alojamiento antes tienes que visitarlo.", ex.getMessage());
        // Verificamos que no se guardó nada
        verify(valoracionRepo, never()).save(any());
    }

    @Test
    void guardarValoracion_Nueva_DeberiaCrearYCalcularMedia() {
        // GIVEN
        // 1. Permiso concedido
        when(reservaRepo.haEmpezadoEstancia(eq(1L), eq(10L), any(LocalDate.class))).thenReturn(true);
        // 2. No existe valoración previa
        when(valoracionRepo.findByUsuario_IdAndInmueble_Id(1L, 10L)).thenReturn(Optional.empty());
        // 3. Mocks de búsqueda
        when(alojamientoRepo.findById(10L)).thenReturn(Optional.of(alojamiento));
        when(userRepo.findById(1L)).thenReturn(Optional.of(usuario));
        // 4. Mock del save (devuelve el mismo objeto)
        when(valoracionRepo.save(any(ValoracionInmueble.class))).thenAnswer(i -> i.getArguments()[0]);
        // 5. Mock de la media
        when(valoracionRepo.obtenerMediaPuntuacion(10L)).thenReturn(5.0);

        // WHEN
        Map<String, Object> resultado = valoracionService.guardarValoracion(10L, 1L, 5.0, "Excelente");

        // THEN
        assertNotNull(resultado.get("valoracion"));
        assertEquals(5.0, resultado.get("nuevaMedia"));
        
        // Verificamos que se llamó a save()
        verify(valoracionRepo).save(any(ValoracionInmueble.class));
    }

    @Test
    void guardarValoracion_Actualizar_DeberiaModificarExistente() {
        // GIVEN
        when(reservaRepo.haEmpezadoEstancia(eq(1L), eq(10L), any(LocalDate.class))).thenReturn(true);
        
        // Simulamos valoración existente
        ValoracionInmueble existente = new ValoracionInmueble(alojamiento, usuario, 2.0, "Malo");
        when(valoracionRepo.findByUsuario_IdAndInmueble_Id(1L, 10L)).thenReturn(Optional.of(existente));
        
        when(valoracionRepo.save(any(ValoracionInmueble.class))).thenAnswer(i -> i.getArguments()[0]);
        when(valoracionRepo.obtenerMediaPuntuacion(10L)).thenReturn(4.0);

        // WHEN: Enviamos nuevos datos (4.0, "Mejoró")
        valoracionService.guardarValoracion(10L, 1L, 4.0, "Mejoró");

        // THEN
        // Verificamos que el objeto existente cambió
        assertEquals(4.0, existente.getPuntuacion());
        assertEquals("Mejoró", existente.getComentario());
        
        // NO se debe buscar Alojamiento ni User de nuevo (optimización)
        verify(alojamientoRepo, never()).findById(anyLong());
    }

    // --------------------------------------------------------
    // Tests: CALCULAR MEDIA
    // --------------------------------------------------------

    @Test
    void obtenerMedia_SinValoraciones_DeberiaDevolverCero() {
        when(valoracionRepo.obtenerMediaPuntuacion(99L)).thenReturn(null);
        Double media = valoracionService.obtenerMedia(99L);
        assertEquals(0.0, media);
    }

    @Test
    void obtenerMedia_ConDecimales_DeberiaRedondear() {
        // Tu lógica hace: Math.round(media * 10.0) / 10.0
        when(valoracionRepo.obtenerMediaPuntuacion(10L)).thenReturn(4.6666);
        
        Double media = valoracionService.obtenerMedia(10L);
        
        assertEquals(4.7, media);
    }
}