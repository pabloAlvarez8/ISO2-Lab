package inmobiliaria.es.uclm.negocio.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Interfaz de repositorio para la gestión de persistencia de la entidad {@link User}.
 * <p>
 * Extiende {@link JpaRepository} para heredar operaciones CRUD estándar, paginación y ordenación
 * sin necesidad de implementar los métodos manualmente. Spring Data JPA genera la implementación
 * en tiempo de ejecución.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Recupera una entidad de usuario basándose en su dirección de correo electrónico exacta.
     * Utiliza el mecanismo de "Query Methods" de Spring Data para derivar la consulta SQL automáticamente.
     *
     * @param email El correo electrónico único del usuario a buscar.
     * @return Un {@link Optional} que contiene el usuario si se encuentra, o vacío si no existe.
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica de manera eficiente si existe algún registro en la base de datos asociado al email proporcionado.
     * Ideal para validaciones previas al registro (evitar duplicados) sin necesidad de cargar la entidad completa.
     *
     * @param email El correo electrónico a comprobar.
     * @return true si el email ya está registrado en el sistema, false en caso contrario.
     */
    boolean existsByEmail(String email);
}