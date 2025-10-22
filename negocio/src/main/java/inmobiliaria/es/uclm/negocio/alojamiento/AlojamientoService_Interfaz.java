package inmobiliaria.es.uclm.negocio.alojamiento;


import inmobiliaria.es.uclm.negocio.alojamiento.dto.DestinoDTO; // Importa el DTO

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AlojamientoService_Interfaz {
    
    List<Alojamiento> listarTodos();
    void guardar(Alojamiento alojamiento);
    void eliminar(Long id);
    List<Alojamiento> buscarPorCiudad(String ciudad);
    Optional<Alojamiento> buscarPorId(Long id); // (Este lo añadimos en la respuesta anterior)

    List<DestinoDTO> obtenerDestinosPopulares();

    List<Alojamiento> buscarConFiltros(
        String ciudad, 
        BigDecimal maxPrice, 
        Double minRating, 
        List<String> types, 
        int capacity, 
        String sortBy
    );
}