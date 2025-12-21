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
import jakarta.persistence.criteria.*;
import org.mockito.ArgumentCaptor;

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

    @Test
    @DisplayName("Specification Coverage: Verify all filters are applied 🧩")
    void searchWithFilters_FullCoverage_ExecutesSpecification() {
        // 1. GIVEN: Preparamos los mocks de JPA
        Root<Alojamiento> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Path<Object> pathMock = mock(Path.class);

        // Mock Predicate para evitar que se devuelvan NULLs y rompan el array
        Predicate dummyPredicate = mock(Predicate.class);

        // STUBBING: Configuramos el comportamiento para devolver el predicado dummy
        when(root.get(anyString())).thenReturn(pathMock);

        // Importante: Hacemos que like, lessThan, etc. devuelvan algo que no sea null
        when(cb.like(any(), anyString())).thenReturn(dummyPredicate);
        when(cb.lessThanOrEqualTo(any(), any(BigDecimal.class))).thenReturn(dummyPredicate);
        when(cb.greaterThanOrEqualTo(any(), anyDouble())).thenReturn(dummyPredicate);
        when(cb.greaterThanOrEqualTo(any(), anyInt())).thenReturn(dummyPredicate);

        // Simulamos el .in()
        CriteriaBuilder.In<Object> inClause = mock(CriteriaBuilder.In.class);
        when(pathMock.in(anyCollection())).thenReturn(inClause);

        // 2. WHEN
        String ciudad = "Madrid";
        BigDecimal precio = BigDecimal.TEN;
        Double rating = 4.5;
        List<String> tipos = List.of("Casa");
        int capacidad = 3;

        service.buscarConFiltros(ciudad, precio, rating, tipos, capacidad, "price_asc");

        // 3. CAPTURA
        ArgumentCaptor<Specification<Alojamiento>> captor = ArgumentCaptor.forClass(Specification.class);
        verify(repo).findAll(captor.capture(), any(Sort.class));

        Specification<Alojamiento> specCapturada = captor.getValue();

        // 4. EJECUCIÓN MANUAL
        specCapturada.toPredicate(root, query, cb);

        // 5. THEN: Verificaciones
        verify(cb).like(any(), contains("madrid"));
        verify(cb).lessThanOrEqualTo(any(), eq(precio));

        // Verifica llamadas numéricas (usamos any() en el primer arg para simplificar)
        verify(cb, atLeastOnce()).greaterThanOrEqualTo(any(), eq(rating));
        verify(cb, atLeastOnce()).greaterThanOrEqualTo(any(), eq(capacidad));

        verify(pathMock).in(tipos);

        // CORRECCIÓN FINAL: Usamos any(Predicate[].class) para soportar VarArgs
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("Specification Coverage: Verify NULL filters skip logic (Branch Coverage) 🔀")
    void searchWithFilters_NullFilters_SkipsAllPredicates() {
        // 1. GIVEN: Mocks básicos
        Root<Alojamiento> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        // Aunque no añadamos nada, al final se llama a cb.and(), así que devolvemos un dummy
        Predicate dummyPredicate = mock(Predicate.class);
        when(cb.and(any(Predicate[].class))).thenReturn(dummyPredicate);

        // 2. WHEN: Llamamos con TODOS los valores en NULL / Vacío / 0
        service.buscarConFiltros(
                null,   // ciudad null
                null,   // precio null
                null,   // rating null
                null,   // tipos null
                0,      // capacidad 0 (tu if dice > 1)
                "none"
        );

        // 3. CAPTURA
        ArgumentCaptor<Specification<Alojamiento>> captor = ArgumentCaptor.forClass(Specification.class);
        verify(repo).findAll(captor.capture(), any(Sort.class));
        Specification<Alojamiento> specCapturada = captor.getValue();

        // 4. EJECUCIÓN MANUAL
        specCapturada.toPredicate(root, query, cb);

        // 5. THEN: Verificamos que NUNCA se llamaron a los constructores de filtros
        // Esto confirma que los 'if' evaluaron a FALSE y saltaron el código

        verify(cb, never()).like(any(), anyString());            // if (ciudad...) saltado
        verify(cb, never()).lessThanOrEqualTo(any(), any(BigDecimal.class)); // if (maxPrice...) saltado
        verify(cb, never()).greaterThanOrEqualTo(any(), anyDouble()); // if (rating...) saltado
        // Nota: para capacidad usamos anyInt() porque es primitivo
        verify(cb, never()).greaterThanOrEqualTo(any(), anyInt());    // if (capacity...) saltado

        // Verifica que NO se llamó a root.get("tipo") ni .in(...)
        // (Asumiendo que no mockeamos root.get para este test específico o verificamos in)

        // Al final, se debe llamar a cb.and() pero con un array vacío
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("Specification Coverage: Edge Cases (Empty Strings, Empty Lists, Zero) ⚠️")
    void searchWithFilters_EdgeCases_EmptyValues_SkipsPredicates() {
        // 1. GIVEN: Mocks básicos necesarios para que no explote
        Root<Alojamiento> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        // Mock para el return final
        Predicate dummyPredicate = mock(Predicate.class);
        when(cb.and(any(Predicate[].class))).thenReturn(dummyPredicate);

        // 2. WHEN: Llamamos con valores que NO son null, pero son "vacíos" o insuficientes
        service.buscarConFiltros(
                "",                     // ciudad: No es null, pero es Empty -> Falla el &&
                null,                   // maxPrice
                0.0,                    // minRating: No es null, pero no es > 0 -> Falla el &&
                Collections.emptyList(),// types: No es null, pero es Empty -> Falla el &&
                1,                      // capacity: Es 1, pero la condición pide > 1 -> Falla
                "none"
        );

        // 3. CAPTURA
        ArgumentCaptor<Specification<Alojamiento>> captor = ArgumentCaptor.forClass(Specification.class);
        verify(repo).findAll(captor.capture(), any(Sort.class));
        Specification<Alojamiento> specCapturada = captor.getValue();

        // 4. EJECUCIÓN MANUAL
        specCapturada.toPredicate(root, query, cb);

        // 5. THEN: Verificamos que NO se añadieron filtros
        // Aunque las variables no eran null, no cumplían la segunda condición

        verify(cb, never()).like(any(), anyString());
        verify(cb, never()).greaterThanOrEqualTo(any(), anyDouble());
        verify(cb, never()).greaterThanOrEqualTo(any(), anyInt());

        // Verifica que NO se intentó acceder a "tipo" ni hacer .in()
        // (Esto confirma que !types.isEmpty() funcionó correctamente)
        verify(root, never()).get("tipo");

        // Al final siempre se llama a and() con lista vacía
        verify(cb).and(any(Predicate[].class));
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