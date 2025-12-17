package inmobiliaria.es.uclm.negocio.alojamiento;

import inmobiliaria.es.uclm.negocio.alojamiento.dto.DestinoDTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlojamientoService implements AlojamientoService_Interfaz {

    private final AlojamientoRepository repo;

    public AlojamientoService(AlojamientoRepository repo) {
        this.repo = repo;
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
                .map(lista -> lista.get(0))
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

            // 1. Filtro de Ciudad
            if (ciudad != null && !ciudad.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("ciudad")),
                        "%" + ciudad.toLowerCase() + "%"));
            }

            // 2. Filtro de Precio
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("precio"), maxPrice));
            }

            // 3. Filtro de Puntuación
            if (minRating != null && minRating > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("valoracionMedia"), minRating));
            }

            // 4. Filtro de Capacidad
            if (capacity > 1) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("capacidad"), capacity));
            }

            // 5. Filtro de Tipos (YA INTEGRADO Y DESCOMENTADO)
            if (types != null && !types.isEmpty()) {
                // Esto crea una cláusula "WHERE tipo IN ('Hotel', 'Apartamento', ...)"
                predicates.add(root.get("tipo").in(types));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // ORDENACIÓN
        Sort sort = Sort.unsorted();
        if ("price_asc".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "precio");
        } else if ("price_desc".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "precio");
        }

        return repo.findAll(spec, sort);
    }

    // --- Obtener Precio Máximo ---
    @Override
    public long obtenerPrecioMaximoAlojamientoRedondeado() {
        BigDecimal maxPrecioBd = repo.findMaxPrecio();

        if (maxPrecioBd == null) {
            return 1000L;
        }

        return (long) Math.ceil(maxPrecioBd.doubleValue());
    }

    // --- Obtener Lista de Tipos ---
    @Override
    public List<String> obtenerTodosLosTipos() {
        return repo.findAllTipos();
    }
}