package inmobiliaria.es.uclm.negocio.valoracion;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import inmobiliaria.es.uclm.negocio.alojamiento.AlojamientoRepository;
import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import inmobiliaria.es.uclm.negocio.user.UserRepository;
import inmobiliaria.es.uclm.negocio.user.User;

@Service
public class ValoracionService {
    @Autowired private ValoracionRepository valoracionRepo;
    @Autowired private AlojamientoRepository alojamientoRepo;
    @Autowired private UserRepository userRepo;

    public ValoracionInmueble crearValoracion(Long inmuebleId, Long usuarioId, Integer puntuacion, String comentario) {
        Alojamiento alojamiento = alojamientoRepo.findById(inmuebleId).orElseThrow();
        User usuario = userRepo.findById(usuarioId).orElseThrow();
        return valoracionRepo.save(new ValoracionInmueble(alojamiento, usuario, puntuacion, comentario));
    }

    public List<ValoracionInmueble> obtenerPorAlojamiento(Long inmuebleId) {
        return valoracionRepo.findByInmuebleId(inmuebleId);
    }
}