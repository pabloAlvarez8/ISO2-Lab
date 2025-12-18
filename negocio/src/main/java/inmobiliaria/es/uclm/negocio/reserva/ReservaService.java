package inmobiliaria.es.uclm.negocio.reserva;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    // 1. Quitamos el @Autowired de aquí y hacemos la variable 'final'
    private final ReservaRepository reservaRepository;

    // 2. Creamos el constructor (Spring inyecta aquí automáticamente el repositorio)
    // Esta es la forma recomendada profesionalmente.
    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    // --- MÉTODOS ---

    public Reserva guardar(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    // Se usará en el PagoController (desaparecerá el aviso gris pronto)
    public Reserva findById(Long id) {
    return reservaRepository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        reservaRepository.deleteById(id);
    }

    // Se usará en el perfil del usuario
    public List<Reserva> obtenerReservasDeUsuario(Long idUsuario) {
        return reservaRepository.findByInquilino_Id(idUsuario);
    }

    // Se usará en el perfil del anfitrión
    public List<Reserva> obtenerReservasDeAnfitrion(Long idAnfitrion) {
        return reservaRepository.findByAlojamiento_Anfitrion_Id(idAnfitrion);
    }

    
}