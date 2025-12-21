package inmobiliaria.es.uclm.negocio.alojamiento;

import inmobiliaria.es.uclm.negocio.alojamiento.dto.DestinoDTO;
import inmobiliaria.es.uclm.negocio.user.User;
import inmobiliaria.es.uclm.negocio.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlojamientoServiceImplTest {

    @Mock
    private AlojamientoRepository repo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private AlojamientoServiceImpl service;

    // ==========================================
    // BASIC CRUD TESTS
    // ==========================================

    @Test
    @DisplayName("findById: When ID exists, returns property ✅")
    void findById_WhenIdExists_ReturnsProperty() {
        Long id = 1L;
        Alojamiento property = new Alojamiento();
        property.setId(id);
        when(repo.findById(id)).thenReturn(Optional.of(property));

        Alojamiento result = service.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    @DisplayName("findById: When ID does not exist, returns null ❌")
    void findById_WhenIdDoesNotExist_ReturnsNull() {
        Long id = 99L;
        when(repo.findById(id)).thenReturn(Optional.empty());
        assertNull(service.findById(id));
    }

    @Test
    @DisplayName("findAll: Returns list of properties 📋")
    void findAll_ReturnsPropertyList() {
        when(repo.findAll()).thenReturn(List.of(new Alojamiento(), new Alojamiento()));
        List<Alojamiento> result = service.listarTodos();
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("findPropertiesByHost: Returns list for specific host 👤")
    void findPropertiesByHost_ReturnsList() {
        Long hostId = 10L;
        when(repo.findByAnfitrion_Id(hostId)).thenReturn(List.of(new Alojamiento()));
        List<Alojamiento> result = service.listarAlojamientosDeAnfitrion(hostId);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("searchByCity: Valid city returns matches 🏙️")
    void searchByCity_ValidCity_ReturnsList() {
        String city = "Madrid";
        Alojamiento house = new Alojamiento();
        house.setCiudad("Madrid");
        when(repo.findByCiudadContainingIgnoreCase(city)).thenReturn(Collections.singletonList(house));

        List<Alojamiento> result = service.buscarPorCiudad(city);

        assertFalse(result.isEmpty());
        assertEquals("Madrid", result.getFirst().getCiudad());
    }

    // ==========================================
    // MANAGEMENT TESTS (SAVE / DELETE)
    // ==========================================

    @Test
    @DisplayName("save: Simple save calls repository 💾")
    void save_ValidProperty_CallsRepo() {
        Alojamiento property = new Alojamiento();
        service.guardar(property);
        verify(repo).save(property);
    }

    @Test
    @DisplayName("delete: Calls repository delete by ID 🗑️")
    void delete_ValidId_CallsRepo() {
        Long id = 5L;
        service.eliminar(id);
        verify(repo).deleteById(id);
    }

    @Test
    @DisplayName("saveNewProperty: Valid user updates role and saves property 🌟")
    void saveNewProperty_ValidUser_UpdatesRoleAndSaves() {
        String email = "test@user.com";
        User normalUser = new User();
        normalUser.setEmail(email);
        normalUser.setRole(User.Role.INQUILINO);

        Alojamiento newProperty = new Alojamiento();
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(normalUser));

        service.guardarNuevoAlojamiento(newProperty, email);

        assertNotNull(newProperty.getAnfitrion());
        assertEquals(User.Role.PROPIETARIO, normalUser.getRole());
        verify(repo).save(newProperty);
        verify(userRepo).save(normalUser);
    }

    @Test
    @DisplayName("saveNewProperty: User not found throws exception ⚠️")
    void saveNewProperty_UserNotFound_ThrowsException() {
        String email = "ghost@user.com";
        Alojamiento newProperty = new Alojamiento();
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.guardarNuevoAlojamiento(newProperty, email));
        verify(repo, never()).save(any());
    }

    // ==========================================
    // FILTER TESTS (CORREGIDOS Y ADAPTADOS)
    // ==========================================

    @Test
    @DisplayName("searchWithFilters: Filters by Rating in MEMORY correctly 🌟")
    void searchWithFilters_FiltersByRating_InMemory() {
        // 1. GIVEN: Dos alojamientos, uno bueno y uno malo
        Alojamiento malo = spy(new Alojamiento());
        doReturn(2.0).when(malo).getValoracionMedia();
        malo.setNombre("Hotel Malo");

        Alojamiento bueno = spy(new Alojamiento());
        doReturn(4.8).when(bueno).getValoracionMedia();
        bueno.setNombre("Hotel Bueno");

        // El repositorio devuelve AMBOS (porque el filtro SQL no incluye rating)
        when(repo.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(malo, bueno));

        // 2. WHEN: Buscamos con filtro de rating mínimo 4.5
        List<Alojamiento> result = service.buscarConFiltros(
                "Madrid", null, 4.5, null, 1,
                null, null, "price_asc"
        );

        // 3. THEN: El servicio debió filtrar en memoria el malo
        assertEquals(1, result.size());
        assertEquals("Hotel Bueno", result.getFirst().getNombre());
        
        // Verificamos que se llamó al repo con alguna Specification, pero no necesitamos
        // verificar el interior de la Specification (Caja Negra vs Caja Blanca)
        verify(repo).findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    @DisplayName("searchWithFilters: Returns empty if dates provided but no availability 📅")
    void searchWithFilters_NoAvailability_ReturnsEmpty() {
        // 1. GIVEN
        LocalDate checkin = LocalDate.now();
        LocalDate checkout = LocalDate.now().plusDays(2);

        // Simulamos que repo.buscarDisponibles devuelve lista vacía
        when(repo.buscarDisponibles(any(), any(), eq(checkin), eq(checkout)))
                .thenReturn(Collections.emptyList());

        // 2. WHEN
        List<Alojamiento> result = service.buscarConFiltros(
                "Madrid", null, null, null, 1, checkin, checkout, "price_asc"
        );

        // 3. THEN
        assertTrue(result.isEmpty());
        // Importante: No debe llamar a findAll si ya sabe que no hay disponibilidad
        verify(repo, never()).findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    @DisplayName("searchWithFilters: Edge Cases - Null Dates Logic 🔀")
    void searchWithFilters_NullDates_CallsFindAll() {
        // 1. GIVEN
        when(repo.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        // 2. WHEN: Fechas nulas
        service.buscarConFiltros(
                null, null, null, null, 0,
                null, null, "none"
        );

        // 3. THEN: Debe llamar a findAll directamente sin pasar por buscarDisponibles
        verify(repo, never()).buscarDisponibles(any(), any(), any(), any());
        verify(repo).findAll(any(Specification.class), any(Sort.class));
    }

    // ==========================================
    // STATS & METADATA TESTS
    // ==========================================

    @Test
    @DisplayName("getPopularDestinations: Returns top cities distinct 🌍")
    void getPopularDestinations_ReturnsTopCities() {
        Alojamiento a1 = new Alojamiento(); a1.setCiudad("Madrid"); a1.setFotoUrl("img1.jpg");
        Alojamiento a2 = new Alojamiento(); a2.setCiudad("Madrid"); a2.setFotoUrl("img2.jpg");
        Alojamiento a3 = new Alojamiento(); a3.setCiudad("Barcelona"); a3.setFotoUrl("img3.jpg");

        when(repo.findAll()).thenReturn(Arrays.asList(a1, a2, a3));

        List<DestinoDTO> result = service.obtenerDestinosPopulares();

        assertEquals(2, result.size());
        // Verificamos presencia
        List<String> ciudades = result.stream().map(DestinoDTO::ciudad).toList();
        assertTrue(ciudades.contains("Madrid"));
        assertTrue(ciudades.contains("Barcelona"));
    }

    @Test
    @DisplayName("getMaxPrice: Normal value returns rounded up 💰")
    void getMaxPrice_NormalValue_ReturnsRoundedUp() {
        when(repo.findMaxPrecio()).thenReturn(new BigDecimal("150.10"));
        long maxPrice = service.obtenerPrecioMaximoAlojamientoRedondeado();
        assertEquals(151L, maxPrice);
    }

    @Test
    @DisplayName("getMaxPrice: Null value returns zero 📉")
    void getMaxPrice_NullValue_ReturnsZero() {
        when(repo.findMaxPrecio()).thenReturn(null);
        long maxPrice = service.obtenerPrecioMaximoAlojamientoRedondeado();
        assertEquals(0L, maxPrice);
    }

    // ==========================================
    // SORT TESTS
    // ==========================================

    @Test
    @DisplayName("searchWithFilters: Sort ASC creates correct sort object 🔼")
    void searchWithFilters_SortAsc_CreatesCorrectSort() {
        String sortBy = "price_asc";
        service.buscarConFiltros(null, null, null, null, 0, null, null, sortBy);

        verify(repo).findAll(any(Specification.class), argThat((Sort s) -> {
            Sort.Order order = s.getOrderFor("precio");
            return order != null && order.isAscending();
        }));
    }

    @Test
    @DisplayName("searchWithFilters: Sort DESC creates correct sort object 🔽")
    void searchWithFilters_SortDesc_CreatesCorrectSort() {
        String sortBy = "price_desc";
        service.buscarConFiltros(null, null, null, null, 0, null, null, sortBy);

        verify(repo).findAll(any(Specification.class), argThat((Sort s) -> {
            Sort.Order order = s.getOrderFor("precio");
            return order != null && order.isDescending();
        }));
    }
}