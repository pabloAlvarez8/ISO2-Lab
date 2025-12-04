package inmobiliaria.es.uclm.negocio.reserva;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    // 1. Para el INQUILINO: Ver sus viajes
    List<Reserva> findByInquilino_Id(Long idUsuario);

    // 2. Para el ANFITRIÓN: Ver quién le ha reservado
    // Buscamos reservas donde el alojamiento pertenezca al anfitrión X
    List<Reserva> findByAlojamiento_Anfitrion_Id(Long idAnfitrion);
}