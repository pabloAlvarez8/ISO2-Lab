package inmobiliaria.es.uclm.negocio.alojamiento;

import inmobiliaria.es.uclm.negocio.alojamiento.dto.DestinoDTO; // Asegúrate de tener este DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// (1) Anotación de Servicio
@Service
public class AlojamientoService implements AlojamientoService_Interfaz {

    // (2) El Repositorio Inyectado
    private final AlojamientoRepository repo;

    @Autowired
    public AlojamientoService(AlojamientoRepository repo) {
        this.repo = repo;
    }

    // --- MÉTODOS QUE FALTABAN (Según el error) ---

    @Override
    public List<Alojamiento> listarTodos() {
        return repo.findAll();
    }

    @Override
    public void guardar(Alojamiento alojamiento) {
        repo.save(alojamiento);
    }

    @Override
    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    // --- OTROS MÉTODOS QUE DEBES TENER ---

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
                .map(lista -> lista.get(0)) // Coge el primer alojamiento de cada ciudad
                .map(alojamiento -> new DestinoDTO(alojamiento.getCiudad(), alojamiento.getFotoUrl()))
                .limit(6) // Mostramos solo 6 destinos
                .collect(Collectors.toList());
    }

    @Autowired
    private AlojamientoRepository alojamientoRepository;

    // ... (tus otras implementaciones de listarTodos, guardar, etc.)

    @Override
    public List<Alojamiento> buscarConFiltros(
        String ciudad, 
        double maxPrice, 
        double minRating, 
        List<String> types, 
        int capacity, 
        String sortBy
    ) {
        
        // 1. Construimos la especificación (la consulta dinámica)
        Specification<Alojamiento> spec = (root, query, criteriaBuilder) -> {
            
            // 'root' es la tabla Alojamiento
            // 'criteriaBuilder' es para construir las cláusulas (WHERE, AND, OR...)
            
            List<Predicate> predicates = new ArrayList<>();

            // --- Añadimos filtros dinámicamente ---

            // Filtro de Ciudad (solo si 'ciudad' no está vacía)
            if (ciudad != null && !ciudad.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("ciudad")), // Columna 'ciudad' en minúsculas
                    "%" + ciudad.toLowerCase() + "%" // Busca 'ciudad' en cualquier parte del nombre
                ));
            }

            // Filtro de Precio (asumiendo que 1000000 es el valor por defecto si no se filtra)
            if (maxPrice < 1000000) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            // Filtro de Puntuación
            if (minRating > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), minRating));
            }

            // Filtro de Capacidad (>=)
            if (capacity > 1) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("capacity"), capacity));
            }

            // Filtro de Tipos (solo si la lista 'types' no está vacía)
            // Esto crea una cláusula: ... AND type IN ('hotel', 'apartamento')
            if (types != null && !types.isEmpty()) {
                predicates.add(root.get("type").in(types));
            }

            // Combinamos todos los filtros con "AND"
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // 2. Construimos el objeto de Ordenación (Sort)
        Sort sort = Sort.unsorted(); // Por defecto, sin ordenar
        if (sortBy.equals("price_asc")) {
            sort = Sort.by(Sort.Direction.ASC, "price");
        } else if (sortBy.equals("price_desc")) {
            sort = Sort.by(Sort.Direction.DESC, "price");
        } else if (sortBy.equals("rating")) {
            sort = Sort.by(Sort.Direction.DESC, "rating");
        }
        // Si es "recommend", se queda 'unsorted' (o puedes definir un orden por defecto)

        // 3. Ejecutamos la consulta final (Especificación + Ordenación)
        return alojamientoRepository.findAll(spec, sort);
    }
}