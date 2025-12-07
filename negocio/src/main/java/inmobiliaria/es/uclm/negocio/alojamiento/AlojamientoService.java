package inmobiliaria.es.uclm.negocio.alojamiento;

import inmobiliaria.es.uclm.negocio.alojamiento.dto.DestinoDTO;
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

    private final AlojamientoRepository repo;

    public AlojamientoService(AlojamientoRepository repo) {
        this.repo = repo;
    }


    @Override
    public List<Alojamiento> buscarPorCiudad(String ciudad) {
        return repo.findByCiudadContainingIgnoreCase(ciudad);
    }

    
    @Override
    public Optional<Alojamiento> buscarPorId(Long id) { 
        return repo.findById(id); }


    @Override
    public void guardar(Alojamiento alojamiento) { 
        repo.save(alojamiento); }


    @Override
    public void eliminar(Long id) { 
        repo.deleteById(id); }


    @Override
    public List<Alojamiento> listarTodos() { 
        return repo.findAll(); }


    @Override
    public List<Alojamiento> listarAlojamientosDeAnfitrion(Long idUsuario) { 
        return repo.findByAnfitrion_Id(idUsuario); }


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

            // 1.Filtro de Ciudad 
            if (ciudad != null && !ciudad.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("ciudad")), 
                        "%" + ciudad.toLowerCase() + "%"));
            }

            // 2. Filtro de Precio ('precioNoche' a 'precio')
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("precio"), maxPrice));
            }


            
            // 3. Filtro de Puntuación (minRating)
            if (minRating != null && minRating > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("valoracionMedia"), minRating)); // <--
                                                                                                              // ARREGLADO:
                                                                                                              // 'valoracionMedia'
            }
            
            
            // 4. Filtro de Capacidad
            if (capacity > 1) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("capacidad"), capacity)); // <-- ARREGLADO:
                                                                                                       // 'capacidad'
            }

            /* 
            // 5. Filtro de Tipos 
            if (types != null && !types.isEmpty()) {
                predicates.add(root.get("tipo").in(types)); 
            }
            */

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

         

        // ORDENACIÓN SIMPLIFICADA
        Sort sort = Sort.unsorted();
        if ("price_asc".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "precio");
        } else if ("price_desc".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "precio");
        } 

        // Ejecutamos la consulta con los filtros y la ordenación
        return repo.findAll(spec, sort);
    }
}