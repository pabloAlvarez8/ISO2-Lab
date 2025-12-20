package inmobiliaria.es.uclm.negocio.valoracion;

import org.springframework.stereotype.Service;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inmobiliaria.es.uclm.negocio.alojamiento.AlojamientoRepository;
import inmobiliaria.es.uclm.negocio.reserva.ReservaRepository;
import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import inmobiliaria.es.uclm.negocio.user.UserRepository;
import inmobiliaria.es.uclm.negocio.user.User;

@Service
public class ValoracionService {
    @Autowired private ValoracionRepository valoracionRepo;
    @Autowired private AlojamientoRepository alojamientoRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private ReservaRepository reservaRepo;

    public Map<String, Object> guardarValoracion(Long inmuebleId, Long usuarioId, Double puntuacion, String comentario) {
        
        // 1. Permitir desde el día de entrada (fechaEntrada <= Hoy)
        boolean puedeValorar = reservaRepo.haEmpezadoEstancia(usuarioId, inmuebleId, LocalDate.now());
        
        if (!puedeValorar) {
            throw new IllegalStateException("Para poder escribir una reseña de este alojamiento antes tienes que visitarlo.");
        }

        // 2. REGLA DE NEGOCIO: Modificar si existe (Upsert)
        Optional<ValoracionInmueble> existente = valoracionRepo.findByUsuario_IdAndInmueble_Id(usuarioId, inmuebleId);
        ValoracionInmueble valoracion;

        if (existente.isPresent()) {
            // Si ya existe, actualizamos los datos
            valoracion = existente.get();
            valoracion.setPuntuacion(puntuacion);
            valoracion.setComentario(comentario);
            // La fecha se actualizará automáticamente si tienes @PreUpdate en la entidad
        } else {
            // Si no existe, creamos una nueva
            Alojamiento alojamiento = alojamientoRepo.findById(inmuebleId).orElseThrow();
            User usuario = userRepo.findById(usuarioId).orElseThrow();
            valoracion = new ValoracionInmueble(alojamiento, usuario, puntuacion, comentario);
        }

        valoracion = valoracionRepo.save(valoracion);

        // 3. ACTUALIZACIÓN INMEDIATA: Recalcular media
        Double nuevaMedia = obtenerMedia(inmuebleId);

        // Devolvemos ambos datos para que el Front se actualice sin recargar
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("valoracion", valoracion);
        respuesta.put("nuevaMedia", nuevaMedia);
        
        return respuesta;
    }

    public List<ValoracionInmueble> obtenerPorAlojamiento(Long inmuebleId) {
        return valoracionRepo.findByInmuebleIdOrderByCreatedAtDesc(inmuebleId);
    }

    public Double obtenerMedia(Long inmuebleId) {
        Double media = valoracionRepo.obtenerMediaPuntuacion(inmuebleId);
        // Si no hay valoraciones, devolvemos 0.0
        return media != null ? Math.round(media * 10.0) / 10.0 : 0.0;
    }
}