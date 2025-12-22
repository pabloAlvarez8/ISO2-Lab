package inmobiliaria.es.uclm.negocio.alojamiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Importante añadir esto

import java.math.BigDecimal;
import java.time.LocalDate; // Importante añadir esto
import java.util.List;

public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long>, JpaSpecificationExecutor<Alojamiento> {

    // --- MÉTODOS EXISTENTES (NO TOCAR) ---

    // 1. Buscar por ciudad
    List<Alojamiento> findByCiudadContainingIgnoreCase(String ciudad);

    // 2. Buscar por precio
    List<Alojamiento> findByPrecioLessThanEqual(BigDecimal precio);

    // 3. Combinado
    List<Alojamiento> findByCiudadContainingIgnoreCaseAndPrecioLessThanEqual(String ciudad, BigDecimal precio);

    // 4. Buscar por anfitrión
    List<Alojamiento> findByAnfitrion_Id(Long idUsuario);

    @Query("SELECT MAX(a.precio) FROM Alojamiento a")
    BigDecimal findMaxPrecio();

    @Query("SELECT DISTINCT a.tipo FROM Alojamiento a WHERE a.tipo IS NOT NULL")
    List<String> findAllTipos();

    // --- NUEVO MÉTODO PARA BUSCADOR CON FECHAS (ADD THIS) ---

    // En AlojamientoRepository.java

// En AlojamientoRepository.java

    @Query("SELECT a FROM Alojamiento a WHERE " +
            "(:precioMax IS NULL OR a.precio <= :precioMax) AND " +
            "(:capacidad IS NULL OR a.capacidad >= :capacidad) AND " +
            "NOT EXISTS (" +
            "SELECT r FROM Reserva r WHERE " +
            "r.alojamiento.id = a.id AND " +
            "r.fechaEntrada < :fechaSalida AND " +
            "r.fechaSalida > :fechaEntrada" +
            ")")
    List<Alojamiento> buscarDisponibles(
            @Param("precioMax") BigDecimal precioMax,
            @Param("capacidad") Integer capacidad,
            @Param("fechaEntrada") LocalDate fechaEntrada,
            @Param("fechaSalida") LocalDate fechaSalida
    );
}