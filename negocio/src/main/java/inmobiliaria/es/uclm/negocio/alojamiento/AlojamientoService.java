package inmobiliaria.es.uclm.negocio.alojamiento;

// IMPORTANTE: Usamos el nombre original
import inmobiliaria.es.uclm.negocio.alojamiento.dto.DestinoDTO;
import inmobiliaria.es.uclm.negocio.user.User;
import inmobiliaria.es.uclm.negocio.user.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlojamientoService implements AlojamientoService_Interfaz {

    private final AlojamientoRepository repo;
    private final UserRepository userRepo;

    public AlojamientoService(AlojamientoRepository repo, UserRepository userRepo) {
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
    // Mantenemos DestinoDTO (nombre original)
    public List<DestinoDTO> obtenerDestinosPopulares() {
        return repo.findAll().stream()
                .collect(Collectors.groupingBy(Alojamiento::getCiudad))
                .values().stream()
                // Java 21: Usamos getFirst() como sugirió IntelliJ
                .map(List::getFirst)
                // Mapeamos a la clase original DestinoDTO
                .map(alojamiento -> new DestinoDTO(alojamiento.getCiudad(), alojamiento.getFotoUrl()))
                .limit(6)
                .collect(Collectors.toList());
    }

    @Override
    public List<Alojamiento> buscarConFiltros(
            String ciudad,
            BigDecimal maxPrice,
            Double minRating,
            List<String> types,
            int capacity,
            String sortBy) {

        Specification<Alojamiento> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (ciudad != null && !ciudad.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("ciudad")),
                        "%" + ciudad.toLowerCase() + "%"));
            }

            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("precio"), maxPrice));
            }

            if (minRating != null && minRating > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("valoracionMedia"), minRating));
            }

            if (capacity > 1) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("capacidad"), capacity));
            }

            if (types != null && !types.isEmpty()) {
                predicates.add(root.get("tipo").in(types));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.unsorted();
        if ("price_asc".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "precio");
        } else if ("price_desc".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "precio");
        }

        return repo.findAll(spec, sort);
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

        // CORRECCIÓN: Si es null devuelve 0 (para que pase el test)
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