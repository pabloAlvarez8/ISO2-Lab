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

import inmobiliaria.es.uclm.negocio.user.User;
import inmobiliaria.es.uclm.negocio.user.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

@Service
public class AlojamientoService implements AlojamientoService_Interfaz {

    private final AlojamientoRepository repo;
    private final UserRepository userRepo; // <--- 1. AÑADE ESTO

    // 2. MODIFICA EL CONSTRUCTOR PARA QUE RECIBA LOS DOS REPOSITORIOS
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
        // .orElse(null) saca el objeto si existe, o devuelve null si no.
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

            // 1.Filtro de Ciudad
            if (ciudad != null && !ciudad.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("ciudad")),
                        "%" + ciudad.toLowerCase() + "%"));
            }

            // 2. Filtro de Precio 
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

    @Override
    @Transactional
    public void guardarNuevoAlojamiento(Alojamiento alojamiento, String emailAnfitrion) {
        
        // 1. Buscamos al usuario
        User anfitrion = userRepo.findByEmail(emailAnfitrion)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Asignamos el dueño
        alojamiento.setAnfitrion(anfitrion);
        
        // 3. ¡BORRAMOS setCreatedAt y setActive! 
        // La entidad Alojamiento ya lo hace sola en su método @PrePersist.
        // Esto elimina todos tus errores de tipos de fecha.

        // 4. Guardamos
        repo.save(alojamiento);

        // 5. Actualizar rol si hace falta
        if (!"PROPIETARIO".equals(anfitrion.getRole().toString())) {
             // Ajusta esto según si usas Enum o String
             // anfitrion.setRole("PROPIETARIO"); 
             // userRepo.save(anfitrion);
        }
    }

}