package inmobiliaria.es.uclm.negocio.alojamiento;

import inmobiliaria.es.uclm.negocio.alojamiento.dto.DestinoDTO;
import inmobiliaria.es.uclm.negocio.user.User;
import inmobiliaria.es.uclm.negocio.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers; // Importante para corregir el unchecked assignment
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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceImplTest {

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
        verify(repo).findById(id);
    }

    @Test
    @DisplayName("findById: When ID does not exist, returns null ❌")
    void findById_WhenIdDoesNotExist_ReturnsNull() {
        Long id = 99L;
        when(repo.findById(id)).thenReturn(Optional.empty());

        Alojamiento result = service.findById(id);

        assertNull(result);
        verify(repo).findById(id);
    }

    @Test
    @DisplayName("findAll: Returns list of properties 📋")
    void findAll_ReturnsPropertyList() {
        when(repo.findAll()).thenReturn(List.of(new Alojamiento(), new Alojamiento()));

        List<Alojamiento> result = service.listarTodos();

        assertEquals(2, result.size());
        verify(repo).findAll();
    }

    @Test
    @DisplayName("findPropertiesByHost: Returns list for specific host 👤")
    void findPropertiesByHost_ReturnsList() {
        Long hostId = 10L;
        when(repo.findByAnfitrion_Id(hostId)).thenReturn(List.of(new Alojamiento()));

        List<Alojamiento> result = service.listarAlojamientosDeAnfitrion(hostId);

        assertFalse(result.isEmpty());
        verify(repo).findByAnfitrion_Id(hostId);
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

        Exception exception = assertThrows(RuntimeException.class,
                () -> service.guardarNuevoAlojamiento(newProperty, email));

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("saveNewProperty: When user is already PROPIETARIO, role is not updated 🛑")
    void saveNewProperty_AlreadyOwner_DoesNotUpdateRole() {
        // GIVEN
        String email = "owner@user.com";
        User ownerUser = new User();
        ownerUser.setEmail(email);
        ownerUser.setRole(User.Role.PROPIETARIO); // Ya es propietario

        Alojamiento newProperty = new Alojamiento();
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(ownerUser));

        // WHEN
        service.guardarNuevoAlojamiento(newProperty, email);

        // THEN
        verify(repo).save(newProperty); // La casa se guarda
        // VERIFICACIÓN CLAVE: No se debe llamar a save del usuario porque no cambió nada
        verify(userRepo, never()).save(ownerUser);
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
        assertTrue(result.stream().anyMatch(d -> d.ciudad().equals("Madrid")));
        assertTrue(result.stream().anyMatch(d -> d.ciudad().equals("Barcelona")));
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

    @Test
    @DisplayName("getAllTypes: Delegates to repository 🏷️")
    void getAllTypes_ReturnsDistinctList() {
        List<String> types = List.of("Casa", "Piso");
        when(repo.findAllTipos()).thenReturn(types);
        List<String> result = service.obtenerTodosLosTipos();
        assertEquals(types, result);
        verify(repo).findAllTipos();
    }

    // ==========================================
    // FILTER & SPECIFICATION TESTS (CORREGIDOS)
    // ==========================================

    @Test
    @DisplayName("searchWithFilters: Sort ASC creates correct sort object 🔼")
    void searchWithFilters_SortAsc_CreatesCorrectSort() {
        String sortBy = "price_asc";

        service.buscarConFiltros(null, null, null, null, 0, sortBy);

        // CORRECCIÓN 1: Usamos ArgumentMatchers.<Type>any() para evitar 'Unchecked assignment'
        // CORRECCIÓN 2: Bloque lambda explícito para evitar warning de NPE
        verify(repo).findAll(ArgumentMatchers.<Specification<Alojamiento>>any(), argThat((Sort s) -> {
            Sort.Order order = s.getOrderFor("precio");
            return order != null && order.isAscending();
        }));
    }

    @Test
    @DisplayName("searchWithFilters: Sort DESC creates correct sort object 🔽")
    void searchWithFilters_SortDesc_CreatesCorrectSort() {
        String sortBy = "price_desc";

        service.buscarConFiltros(null, null, null, null, 0, sortBy);

        verify(repo).findAll(ArgumentMatchers.<Specification<Alojamiento>>any(), argThat((Sort s) -> {
            Sort.Order order = s.getOrderFor("precio");
            return order != null && order.isDescending();
        }));
    }

    @Test
    @DisplayName("searchWithFilters: All filters execute without error 🔍")
    void searchWithFilters_AllFilters_ExecutesWithoutError() {
        String city = "Madrid";
        BigDecimal maxPrice = BigDecimal.valueOf(100);
        Double rating = 4.0;
        List<String> types = List.of("Apartamento");
        int capacity = 2;
        String sortBy = "none";

        service.buscarConFiltros(city, maxPrice, rating, types, capacity, sortBy);

        verify(repo).findAll(ArgumentMatchers.<Specification<Alojamiento>>any(), any(Sort.class));
    }
}