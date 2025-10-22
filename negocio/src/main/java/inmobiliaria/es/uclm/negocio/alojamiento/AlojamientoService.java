package inmobiliaria.es.uclm.negocio.alojamiento;

import inmobiliaria.es.uclm.negocio.alojamiento.dto.DestinoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlojamientoService implements AlojamientoService_Interfaz {

    // Única inyección del Repositorio
    private final AlojamientoRepository repo;

    @Autowired
    public AlojamientoService(AlojamientoRepository repo) {
        this.repo = repo;
    }

    // ... (métodos listarTodos, guardar, eliminar, etc. van aquí) ...
    // ... (método obtenerDestinosPopulares va aquí) ...

    @Override
    public List<Alojamiento> buscarPorCiudad(String ciudad) {
        return repo.findByCiudad(ciudad);
    }
    
    @Override
    public Optional<Alojamiento> buscarPorId(Long id) {
        return repo.findById(id);
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


    // --- MÉTODO CORREGIDO ---
    @Override
    public List<Alojamiento> buscarConFiltros(
            String ciudad, 
            BigDecimal maxPrice, 
            Double minRating,  // <-- Asegúrate que la Interfaz también use 'Double' (objeto)
            List<String> types, 
            int capacity, 
            String sortBy
    ) {
        
        Specification<Alojamiento> spec = (root, query, criteriaBuilder) -> {
            
            List<Predicate> predicates = new ArrayList<>();

            // Filtro de Ciudad
            if (ciudad != null && !ciudad.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("ciudad")), // <-- Nombre de campo Java
                        "%" + ciudad.toLowerCase() + "%"
                ));
            }

            // Filtro de Precio
            if (maxPrice != null) { // <-- ARREGLADO: Así se comprueba si el filtro se aplica
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("precio"), maxPrice)); // <-- ARREGLADO: 'precio'
            }

            // Filtro de Puntuación (minRating)
            if (minRating != null && minRating > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("valoracionMedia"), minRating)); // <-- ARREGLADO: 'valoracionMedia'
            }

            // Filtro de Capacidad
            if (capacity > 1) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("capacidad"), capacity)); // <-- ARREGLADO: 'capacidad'
            }

            // Filtro de Tipos
            if (types != null && !types.isEmpty()) {
                predicates.add(root.get("tipo").in(types)); // <-- ARREGLADO: 'tipo'
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // 2. Construimos el Sort
        Sort sort = Sort.unsorted();
        if ("price_asc".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "precio"); // <-- ARREGLADO: 'precio'
        } else if ("price_desc".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "precio"); // <-- ARREGLADO: 'precio'
        } else if ("rating".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "valoracionMedia"); // <-- ARREGLADO: 'valoracionMedia'
        }

        // 3. Ejecutamos la consulta
        return repo.findAll(spec, sort);
    }
}