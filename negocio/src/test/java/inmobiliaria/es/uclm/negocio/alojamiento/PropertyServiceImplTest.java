package inmobiliaria.es.uclm.negocio.alojamiento;

import inmobiliaria.es.uclm.negocio.alojamiento.dto.DestinoDTO;
import inmobiliaria.es.uclm.negocio.user.User;
import inmobiliaria.es.uclm.negocio.user.UserRepository;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    // 1. TESTS DE BÚSQUEDA BÁSICA Y CRUD
    // ==========================================

    @Test
    @DisplayName("buscarPorCiudad: Retorna lista de alojamientos")
    void buscarPorCiudad_ReturnsList() {
        String ciudad = "Madrid";
        when(repo.findByCiudadContainingIgnoreCase(ciudad)).thenReturn(List.of(new Alojamiento()));

        List<Alojamiento> result = service.buscarPorCiudad(ciudad);

        assertFalse(result.isEmpty());
        verify(repo).findByCiudadContainingIgnoreCase(ciudad);
    }

    @Test
    @DisplayName("findById: Retorna alojamiento si existe")
    void findById_Exists_ReturnsAlojamiento() {
        Long id = 1L;
        Alojamiento alojamiento = new Alojamiento();
        alojamiento.setId(id);
        when(repo.findById(id)).thenReturn(Optional.of(alojamiento));

        Alojamiento result = service.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    @DisplayName("findById: Retorna null si no existe")
    void findById_NotExists_ReturnsNull() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertNull(service.findById(1L));
    }

    @Test
    @DisplayName("guardar: Llama al repositorio")
    void guardar_CallsRepo() {
        Alojamiento a = new Alojamiento();
        service.guardar(a);
        verify(repo).save(a);
    }

    @Test
    @DisplayName("eliminar: Llama al repositorio")
    void eliminar_CallsRepo() {
        service.eliminar(1L);
        verify(repo).deleteById(1L);
    }

    @Test
    @DisplayName("listarTodos: Retorna lista completa")
    void listarTodos_ReturnsAll() {
        service.listarTodos();
        verify(repo).findAll();
    }

    @Test
    @DisplayName("listarAlojamientosDeAnfitrion: Retorna lista filtrada")
    void listarAlojamientosDeAnfitrion_ReturnsList() {
        Long userId = 10L;
        service.listarAlojamientosDeAnfitrion(userId);
        verify(repo).findByAnfitrion_Id(userId);
    }

    // ==========================================
    // 2. TESTS DE LÓGICA DE NEGOCIO (GUARDAR NUEVO)
    // ==========================================

    @Test
    @DisplayName("guardarNuevoAlojamiento: Usuario no encontrado lanza excepción")
    void guardarNuevoAlojamiento_UserNotFound_ThrowsException() {
        Alojamiento a = new Alojamiento();
        String email = "fake@email.com";
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.guardarNuevoAlojamiento(a, email));
    }

    @Test
    @DisplayName("guardarNuevoAlojamiento: Actualiza rol a PROPIETARIO si no lo era")
    void guardarNuevoAlojamiento_UpdatesRole() {
        String email = "user@email.com";
        User user = new User();
        user.setRole(User.Role.INQUILINO);
        
        Alojamiento a = new Alojamiento();

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        service.guardarNuevoAlojamiento(a, email);

        assertEquals(User.Role.PROPIETARIO, user.getRole());
        verify(repo).save(a);
        verify(userRepo).save(user);
    }

    @Test
    @DisplayName("guardarNuevoAlojamiento: No actualiza rol si ya es PROPIETARIO")
    void guardarNuevoAlojamiento_AlreadyOwner_NoRoleUpdate() {
        String email = "owner@email.com";
        User user = new User();
        user.setRole(User.Role.PROPIETARIO);

        Alojamiento a = new Alojamiento();

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        service.guardarNuevoAlojamiento(a, email);

        verify(repo).save(a);
        verify(userRepo, never()).save(user); // No se guarda el usuario porque no cambió
    }

    // ==========================================
    // 3. TESTS DE ESTADÍSTICAS Y UTILIDADES
    // ==========================================

    @Test
    @DisplayName("obtenerDestinosPopulares: Agrupa y devuelve DTOs")
    void obtenerDestinosPopulares_ReturnsDTOs() {
        Alojamiento a1 = new Alojamiento(); a1.setCiudad("Madrid"); a1.setFotoUrl("foto1");
        Alojamiento a2 = new Alojamiento(); a2.setCiudad("Madrid"); a2.setFotoUrl("foto2");
        Alojamiento a3 = new Alojamiento(); a3.setCiudad("Barcelona"); a3.setFotoUrl("foto3");

        when(repo.findAll()).thenReturn(List.of(a1, a2, a3));

        List<DestinoDTO> resultado = service.obtenerDestinosPopulares();

        assertEquals(2, resultado.size()); // Madrid y Barcelona
        assertTrue(resultado.stream().anyMatch(d -> d.ciudad().equals("Madrid")));
    }

    @Test
    @DisplayName("obtenerPrecioMaximo: Devuelve valor redondeado hacia arriba")
    void obtenerPrecioMaximo_ReturnsCeiledValue() {
        when(repo.findMaxPrecio()).thenReturn(new BigDecimal("99.1"));
        assertEquals(100L, service.obtenerPrecioMaximoAlojamientoRedondeado());
    }

    @Test
    @DisplayName("obtenerPrecioMaximo: Devuelve 0 si es null")
    void obtenerPrecioMaximo_ReturnsZeroIfNull() {
        when(repo.findMaxPrecio()).thenReturn(null);
        assertEquals(0L, service.obtenerPrecioMaximoAlojamientoRedondeado());
    }

    @Test
    @DisplayName("obtenerTodosLosTipos: Delega en el repositorio")
    void obtenerTodosLosTipos_CallsRepo() {
        service.obtenerTodosLosTipos();
        verify(repo).findAllTipos();
    }

    // ==========================================
    // 4. TESTS DE BÚSQUEDA AVANZADA (FILTROS)
    // ==========================================

    @Test
    @DisplayName("buscarConFiltros: Sin fechas ni disponibilidad, busca normal")
    void buscarConFiltros_SimpleSearch() {
        when(repo.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        service.buscarConFiltros("Madrid", null, null, null, 1, null, null, "price_asc");

        verify(repo).findAll(any(Specification.class), any(Sort.class));
        verify(repo, never()).buscarDisponibles(any(), any(), any(), any());
    }

    @Test
    @DisplayName("buscarConFiltros: Con fechas, si no hay ids disponibles retorna vacío")
    void buscarConFiltros_DatesProvided_NoAvailability_ReturnsEmpty() {
        LocalDate in = LocalDate.now();
        LocalDate out = LocalDate.now().plusDays(2);
        
        when(repo.buscarDisponibles(any(), any(), eq(in), eq(out))).thenReturn(Collections.emptyList());

        List<Alojamiento> res = service.buscarConFiltros("Madrid", null, null, null, 1, in, out, "price_asc");

        assertTrue(res.isEmpty());
        verify(repo, never()).findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    @DisplayName("buscarConFiltros: Filtra por Puntuación en Memoria")
    void buscarConFiltros_FiltersByRatingInMemory() {
        // Simulamos objetos, necesitamos SPY para getValoracionMedia si no hay lógica real
        Alojamiento bajo = spy(new Alojamiento());
        doReturn(3.0).when(bajo).getValoracionMedia();

        Alojamiento alto = spy(new Alojamiento());
        doReturn(5.0).when(alto).getValoracionMedia();

        when(repo.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(bajo, alto));

        // Pedimos mínimo 4.0
        List<Alojamiento> res = service.buscarConFiltros(null, null, 4.0, null, 0, null, null, null);

        assertEquals(1, res.size());
        assertEquals(alto, res.get(0));
    }

    @Test
    @DisplayName("buscarConFiltros: Ordenación descendente")
    void buscarConFiltros_SortDesc() {
        service.buscarConFiltros(null, null, null, null, 0, null, null, "price_desc");

        ArgumentCaptor<Sort> captor = ArgumentCaptor.forClass(Sort.class);
        verify(repo).findAll(any(Specification.class), captor.capture());
        
        Sort.Order order = captor.getValue().getOrderFor("precio");
        assertNotNull(order);
        assertTrue(order.isDescending());
    }

    // ==========================================
    // 5. SPECIFICATION INTERNALS (COBERTURA 100% DE LÍNEAS PRIVADAS)
    // ==========================================

    @Test
    @DisplayName("Coverage: Verifica construcción de Specification (CriteriaBuilder)")
    void testSpecificationConstruction() {
        // 1. Mocks de JPA Criteria
        Root<Alojamiento> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Path pathMock = mock(Path.class);
        Predicate dummyPredicate = mock(Predicate.class);

        // Configuración de comportamiento de los mocks
        when(root.get(anyString())).thenReturn(pathMock);
        when(cb.like(any(), anyString())).thenReturn(dummyPredicate);
        when(cb.lower(any())).thenReturn(mock(Expression.class)); // Para cb.lower()
        when(cb.lessThanOrEqualTo(any(), any(BigDecimal.class))).thenReturn(dummyPredicate);
        when(cb.greaterThanOrEqualTo(any(), anyInt())).thenReturn(dummyPredicate);
        
        // CORRECCIÓN 1: Usamos any(Predicate[].class) para varargs
        when(cb.and(any(Predicate[].class))).thenReturn(dummyPredicate);
        
        when(pathMock.in(anyCollection())).thenReturn(mock(CriteriaBuilder.In.class)); // Para .in()

        // 2. Preparamos datos que disparen TODOS los 'if' dentro de crearSpecification
        String ciudad = "Madrid";
        BigDecimal maxPrice = BigDecimal.TEN;
        List<String> types = List.of("Hotel");
        int capacity = 2;
        
        // Simulamos IDs disponibles para que entre en ese IF
        LocalDate in = LocalDate.now();
        LocalDate out = in.plusDays(1);
        Alojamiento a = new Alojamiento(); a.setId(5L);
        when(repo.buscarDisponibles(any(), any(), any(), any())).thenReturn(List.of(a));

        // 3. Ejecutamos el servicio
        service.buscarConFiltros(ciudad, maxPrice, null, types, capacity, in, out, "price_asc");

        // 4. Capturamos la Specification que se pasó al repo
        ArgumentCaptor<Specification<Alojamiento>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(repo).findAll(specCaptor.capture(), any(Sort.class));

        Specification<Alojamiento> spec = specCaptor.getValue();

        // 5. ¡LA CLAVE! Ejecutamos manualmente el toPredicate para que corran las líneas privadas
        spec.toPredicate(root, query, cb);

        // 6. Verificamos que se llamaron los métodos del CriteriaBuilder
        verify(cb).like(any(), contains("madrid")); // Filtro ciudad
        verify(cb).lessThanOrEqualTo(any(), eq(maxPrice)); // Filtro precio
        verify(cb).greaterThanOrEqualTo(any(), eq(capacity)); // Filtro capacidad
        
        // CORRECCIÓN 2: Se llama 2 veces: una para IDs y otra para TIPOS
        verify(pathMock, times(2)).in(anyCollection()); 
        
        // Verificamos que se llamó al AND con un array de Predicates
        verify(cb).and(any(Predicate[].class)); 
    }
}