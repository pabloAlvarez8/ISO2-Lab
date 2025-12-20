package inmobiliaria.es.uclm.negocio.alojamiento;

import inmobiliaria.es.uclm.negocio.alojamiento.dto.DestinoDTO;
import inmobiliaria.es.uclm.negocio.user.User;
import inmobiliaria.es.uclm.negocio.user.UserRepository;
import inmobiliaria.es.uclm.negocio.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private AlojamientoRepository propertyRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private AlojamientoService propertyService;

    @Mock
    private UserService userService;

    // --- TESTS EXISTENTES (Los que ya tenías) ---

    @Test
    @DisplayName("searchByCity_ValidCity_ReturnsListOfProperties 🏙️")
    void searchByCity_ValidCity_ReturnsList() {
        // 1. GIVEN
        String city = "Madrid";
        Alojamiento house = new Alojamiento();
        house.setCiudad("Madrid");
        when(propertyRepo.findByCiudadContainingIgnoreCase(city)).thenReturn(Collections.singletonList(house));

        // 2. WHEN
        List<Alojamiento> result = propertyService.buscarPorCiudad(city);

        // 3. THEN
        assertFalse(result.isEmpty(), "La lista no debería estar vacía");
        assertEquals("Madrid", result.getFirst().getCiudad(), "La ciudad debería coincidir");
    }

    @Test
    @DisplayName("saveNewProperty_ValidUser_SavesPropertyAndUpdatesRole 💾")
    void saveNewProperty_ValidUser_SavesPropertyAndUpdatesRole() {
        // 1. GIVEN
        String email = "test@user.com";
        User normalUser = new User();
        normalUser.setEmail(email);
        normalUser.setRole(User.Role.INQUILINO); // Es un usuario normal

        Alojamiento newProperty = new Alojamiento();
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(normalUser));

        // 2. WHEN
        propertyService.guardarNuevoAlojamiento(newProperty, email);

        // 3. THEN
        assertNotNull(newProperty.getAnfitrion(), "El anfitrión debería haberse asignado al alojamiento");
        verify(propertyRepo).save(newProperty); // Se guarda la casa

        // Verificamos que el usuario ahora es PROPIETARIO
        assertEquals(User.Role.PROPIETARIO, normalUser.getRole(), "El rol del usuario debería cambiar a PROPIETARIO");
        verify(userRepo).save(normalUser); // Se guarda el cambio de rol
    }

    @Test
    @DisplayName("saveNewProperty_UserNotFound_ThrowsException ⚠️")
    void saveNewProperty_UserNotFound_ThrowsException() {
        // 1. GIVEN
        String email = "ghost@user.com";
        Alojamiento newProperty = new Alojamiento();
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        // 2. WHEN & 3. THEN
        Exception exception = assertThrows(RuntimeException.class, () -> propertyService.guardarNuevoAlojamiento(newProperty, email));

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(propertyRepo, never()).save(any());
    }

    @Test
    @DisplayName("getPopularDestinations_ReturnsDistinctCitiesDTO 🌍")
    void getPopularDestinations_ReturnsDistinctCitiesDTO() {
        // 1. GIVEN
        Alojamiento a1 = new Alojamiento(); a1.setCiudad("Madrid"); a1.setFotoUrl("img1.jpg");
        Alojamiento a2 = new Alojamiento(); a2.setCiudad("Madrid"); a2.setFotoUrl("img2.jpg");
        Alojamiento a3 = new Alojamiento(); a3.setCiudad("Barcelona"); a3.setFotoUrl("img3.jpg");

        when(propertyRepo.findAll()).thenReturn(Arrays.asList(a1, a2, a3));

        // 2. WHEN
        List<DestinoDTO> result = propertyService.obtenerDestinosPopulares();

        // 3. THEN
        assertEquals(2, result.size(), "Debería haber solo 2 destinos únicos");
        assertTrue(result.stream().anyMatch(dto -> dto.ciudad().equals("Madrid")));
    }

    @Test
    @DisplayName("getMaxPrice_DecimalValue_ReturnsRoundedUp 💰")
    void getMaxPrice_DecimalValue_ReturnsRoundedUp() {
        // 1. GIVEN (La BD devuelve 150.1)
        when(propertyRepo.findMaxPrecio()).thenReturn(new BigDecimal("150.10"));

        // 2. WHEN
        long maxPrice = propertyService.obtenerPrecioMaximoAlojamientoRedondeado();

        // 3. THEN (Se redondea a 151)
        assertEquals(151L, maxPrice, "El precio debería redondearse hacia arriba");
    }

    @Test
    @DisplayName("searchWithFilters_CallsRepositoryWithSpec 🔍")
    void searchWithFilters_CallsRepositoryWithSpec() {
        // 1. GIVEN
        String city = "Valencia";
        BigDecimal maxPrice = new BigDecimal("200");
        Double rating = 4.5;
        List<String> types = List.of("Apartamento");
        int capacity = 2;
        String sortOrder = "price_asc";

        // 2. WHEN
        propertyService.buscarConFiltros(city, maxPrice, rating, types, capacity, sortOrder);

        // 3. THEN
        verify(propertyRepo).findAll(any(Specification.class), any(Sort.class));
    }

    // --- NUEVOS TESTS AÑADIDOS (Lo que faltaba) ---

    @Test
    @DisplayName("getMaxPrice_ReturnsZero_WhenNull 📉")
    void getMaxPrice_ReturnsZero_WhenNull() {
        // 1. GIVEN (Caso borde: no hay casas en la BD, devuelve null)
        when(propertyRepo.findMaxPrecio()).thenReturn(null);

        // 2. WHEN
        long maxPrice = propertyService.obtenerPrecioMaximoAlojamientoRedondeado();

        // 3. THEN
        assertEquals(0L, maxPrice, "Si no hay precio, debería devolver 0");
    }

    @Test
    @DisplayName("getAllTypes_ReturnsDistinctList 📋")
    void getAllTypes_ReturnsDistinctList() {
        // 1. GIVEN
        List<String> tiposMock = Arrays.asList("Piso", "Chalet", "Cabaña");
        when(propertyRepo.findAllTipos()).thenReturn(tiposMock);

        // 2. WHEN
        List<String> resultado = propertyService.obtenerTodosLosTipos();

        // 3. THEN
        assertEquals(3, resultado.size());
        assertTrue(resultado.contains("Cabaña"));
        verify(propertyRepo).findAllTipos();
    }

    @Test
    @DisplayName("searchByHost_ReturnsHostProperties 👤")
    void searchByHost_ReturnsHostProperties() {
        // 1. GIVEN
        Long idAnfitrion = 5L;
        when(propertyRepo.findByAnfitrion_Id(idAnfitrion))
                .thenReturn(Collections.emptyList());

        // 2. WHEN
        // CORREGIDO: Usamos el nombre real que ya tienes en tu servicio
        propertyService.listarAlojamientosDeAnfitrion(idAnfitrion);

        // 3. THEN
        verify(propertyRepo).findByAnfitrion_Id(idAnfitrion);
    }
}