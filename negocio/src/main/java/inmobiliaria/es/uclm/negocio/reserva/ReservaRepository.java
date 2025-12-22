package inmobiliaria.es.uclm.negocio.reserva;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    // 1. Para el INQUILINO: Ver sus viajes
    List<Reserva> findByInquilino_Id(Long idUsuario);

    // 2. Para el ANFITRIÓN: Ver quién le ha reservado
    // Buscamos reservas donde el alojamiento pertenezca al anfitrión X
    List<Reserva> findByAlojamiento_Anfitrion_Id(Long idAnfitrion);

        // 3. Buscar reservas futuras para pintar el calendario (ignora las rechazadas)
    @Query("SELECT r FROM Reserva r WHERE r.alojamiento.id = :alojamientoId " +
        "AND (r.estado IS NULL OR r.estado <> 'RECHAZADA') " +
         "AND r.fechaSalida >= CURRENT_DATE")
    List<Reserva> findReservasFuturas(@Param("alojamientoId") Long alojamientoId);

    // 4. Contar solapamientos para validar una nueva reserva
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.alojamiento.id = :alojamientoId " +
        "AND (r.estado IS NULL OR r.estado <> 'RECHAZADA') " +
        "AND (r.fechaEntrada < :salida AND r.fechaSalida > :entrada)")
    long countSolapamientos(@Param("alojamientoId") Long alojamientoId, 
                            @Param("entrada") LocalDate entrada, 
                            @Param("salida") LocalDate salida);

    // 5. Busca reservas del inquilino cuya fecha de salida sea hoy o futura (evita ver viajes de hace 2 años)
    @Query("SELECT r FROM Reserva r WHERE r.inquilino.id = :idUsuario AND r.fechaSalida >= CURRENT_DATE ORDER BY r.fechaEntrada ASC")
    List<Reserva> findMisProximosViajes(@Param("idUsuario") Long idUsuario);

    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.inquilino.id = :usuarioId " +
           "AND r.alojamiento.id = :alojamientoId " +
           "AND r.fechaEntrada <= :hoy " +
           "AND (r.estado IS NULL OR r.estado <> 'RECHAZADA')")
    boolean haEmpezadoEstancia(@Param("usuarioId") Long usuarioId, 
                               @Param("alojamientoId") Long alojamientoId, 
                               @Param("hoy") LocalDate hoy);
    
    // Comprueba si existe una reserva de este usuario en este alojamiento que ya haya finalizado (fechaSalida < hoy)
    boolean existsByInquilino_IdAndAlojamiento_IdAndFechaSalidaBefore(Long inquilinoId, Long alojamientoId, LocalDate fecha);
}