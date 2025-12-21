package inmobiliaria.es.uclm.negocio.alojamiento;

import inmobiliaria.es.uclm.negocio.alojamiento.dto.DestinoDTO;
import inmobiliaria.es.uclm.negocio.user.User;
import inmobiliaria.es.uclm.negocio.user.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlojamientoServiceImpl implements AlojamientoService {

    private static final Logger logger = LoggerFactory.getLogger(AlojamientoServiceImpl.class);

    public static final String FIELD_PRICE = "precio";
    public static final String FIELD_CITY = "ciudad";
    public static final String FIELD_CAPACITY = "capacidad";

    private final AlojamientoRepository repo;
    private final UserRepository userRepo;

    public AlojamientoServiceImpl(AlojamientoRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    @Override
    public List<Alojamiento> buscarPorCiudad(String ciudad) {
        return repo.findByCiudadContainingIgnoreCase(ciudad);
    }

    @Override
    public Alojamiento findById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public void guardar(Alojamiento alojamiento) {
        repo.save(alojamiento);
    }

    @Override
    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<Alojamiento> listarTodos() {
        return repo.findAll();
    }

    @Override
    public List<Alojamiento> listarAlojamientosDeAnfitrion(Long idUsuario) {
        return repo.findByAnfitrion_Id(idUsuario);
    }

    @Override
    public List<DestinoDTO> obtenerDestinosPopulares() {
        return repo.findAll().stream()
                .collect(Collectors.groupingBy(Alojamiento::getCiudad))
                .values().stream()
                .map(List::getFirst)
                .map(alojamiento -> new DestinoDTO(alojamiento.getCiudad(), alojamiento.getFotoUrl()))
                .limit(6)
                .toList();
    }

    @Override
    public List<Alojamiento> buscarConFiltros(
            String ciudad, BigDecimal maxPrice, Double minRating,
            List<String> types, int capacity, LocalDate checkin,
            LocalDate checkout, String sortBy) {

        List<Long> idsDisponibles = obtenerIdsDisponibles(checkin, checkout, maxPrice, capacity);

        // LÓGICA DE CORTE PRECISA:
        // Solo devolvemos vacío si el usuario BUSCÓ fechas (checkin/out != null)
        // Y el repositorio confirmó que no hay nada libre (lista vacía).
        if (checkin != null && checkout != null && idsDisponibles.isEmpty()) {
            return Collections.emptyList();
        }

        Specification<Alojamiento> spec = crearSpecification(ciudad, maxPrice, minRating, types, capacity, idsDisponibles);
        Sort sort = determinarOrdenacion(sortBy);

        return repo.findAll(spec, sort);
    }

    private List<Long> obtenerIdsDisponibles(LocalDate entrada, LocalDate salida, BigDecimal precio, int cap) {
        // Si falta alguna fecha, devolvemos lista vacía (no hay filtro de fechas)
        if (entrada == null || salida == null) {
            return Collections.emptyList();
        }

        Integer capFiltro = (cap > 0) ? cap : null;
        List<Alojamiento> disponibles = repo.buscarDisponibles(precio, capFiltro, entrada, salida);

        logger.info("Alojamientos LIBRES por fecha: {}", disponibles.size());

        return disponibles.stream().map(Alojamiento::getId).toList();
    }

    // --- INICIO REFACTORIZACIÓN (REDUCCIÓN COMPLEJIDAD COGNITIVA) ---

    private Specification<Alojamiento> crearSpecification(
            String ciudad, BigDecimal maxPrice, Double minRating,
            List<String> types, int capacity, List<Long> ids) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            addIdAndCityPredicates(predicates, cb, root, ids, ciudad);
            addMetricsPredicates(predicates, cb, root, maxPrice, minRating, capacity);
            addTypePredicate(predicates, root, types);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addIdAndCityPredicates(List<Predicate> predicates, CriteriaBuilder cb,
                                        Root<Alojamiento> root, List<Long> ids, String ciudad) {
        // 1. Filtro de IDs (Disponibilidad)
        if (ids != null && !ids.isEmpty()) {
            predicates.add(root.get("id").in(ids));
        }

        // 2. Filtro de Ciudad
        if (ciudad != null && !ciudad.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get(FIELD_CITY)), "%" + ciudad.toLowerCase() + "%"));
        }
    }

    private void addMetricsPredicates(List<Predicate> predicates, CriteriaBuilder cb,
                                      Root<Alojamiento> root, BigDecimal maxPrice,
                                      Double minRating, int capacity) {
        // 3. Filtro de Precio
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            predicates.add(cb.lessThanOrEqualTo(root.get(FIELD_PRICE), maxPrice));
        }

        // 4. Filtro de Puntuación
        if (minRating != null && minRating > 0) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("puntuacion"), minRating));
        }

        // 5. Filtro de Capacidad
        if (capacity > 0) {
            predicates.add(cb.greaterThanOrEqualTo(root.get(FIELD_CAPACITY), capacity));
        }
    }

    private void addTypePredicate(List<Predicate> predicates, Root<Alojamiento> root, List<String> types) {
        // 6. Filtro de Tipos
        if (types != null && !types.isEmpty()) {
            predicates.add(root.get("tipo").in(types));
        }
    }
    
    // --- FIN REFACTORIZACIÓN ---

    private Sort determinarOrdenacion(String sortBy) {
        if ("price_asc".equals(sortBy)) {
            return Sort.by(Sort.Direction.ASC, FIELD_PRICE);
        }
        if ("price_desc".equals(sortBy)) {
            return Sort.by(Sort.Direction.DESC, FIELD_PRICE);
        }
        return Sort.unsorted();
    }

    @Override
    @Transactional
    public void guardarNuevoAlojamiento(Alojamiento alojamiento, String emailAnfitrion) {
        User anfitrion = userRepo.findByEmail(emailAnfitrion)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        alojamiento.setAnfitrion(anfitrion);
        repo.save(alojamiento);

        if (anfitrion.getRole() != User.Role.PROPIETARIO) {
            anfitrion.setRole(User.Role.PROPIETARIO);
            userRepo.save(anfitrion);
        }
    }

    @Override
    public long obtenerPrecioMaximoAlojamientoRedondeado() {
        BigDecimal maxPrecioBd = repo.findMaxPrecio();
        if (maxPrecioBd == null) {
            return 0L;
        }
        return (long) Math.ceil(maxPrecioBd.doubleValue());
    }

    @Override
    public List<String> obtenerTodosLosTipos() {
        return repo.findAllTipos();
    }
}