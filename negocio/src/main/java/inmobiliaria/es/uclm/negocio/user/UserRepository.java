package inmobiliaria.es.uclm.negocio.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// No necesita JpaSpecificationExecutor si solo haces búsquedas simples aquí
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // Busca por campo 'email'
    boolean existsByEmail(String email);     // Comprueba existencia por campo 'email'
}