package inmobiliaria.es.uclm.negocio.alojamiento;

import java.util.List;

public interface AlojamientoService_Interfaz {
    
    List<Alojamiento> listarTodos();          // devuelve todos los alojamientos
    void guardar(Alojamiento alojamiento);    // guarda o actualiza un alojamiento
    void eliminar(Long id);                   // elimina un alojamiento por id
    List<Alojamiento> buscarPorCiudad(String ciudad); // busca alojamientos por ciudad
}
