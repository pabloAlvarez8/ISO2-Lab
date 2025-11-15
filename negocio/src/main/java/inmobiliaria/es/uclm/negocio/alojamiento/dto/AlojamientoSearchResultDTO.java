package inmobiliaria.es.uclm.negocio.alojamiento.dto;

import java.math.BigDecimal;
import java.util.List; // Importa List
import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento; // Importa tu entidad

// Este DTO ahora coincide EXACTAMENTE con lo que 'detalle.js' espera.
public record AlojamientoSearchResultDTO(
    int id,
    String title,         // <-- 'nombre' ahora es 'title'
    String ciudad,
    String type,          // <-- 'tipo' ahora es 'type'
    BigDecimal price,     // <-- 'precio' ahora es 'price'
    List<String> images,  // <-- 'fotoUrl' ahora es un array 'images'
    Double rating,        // <-- 'valoracionMedia' ahora es 'rating'
    int capacity,         // <-- 'capacidad' ahora es 'capacity'
    BigDecimal distance  // <-- AÑADIDO: 'distancia_centro' ahora es 'distance'
) {

    // El método 'factory' se actualiza para hacer el mapeo
    public static AlojamientoSearchResultDTO fromEntity(Alojamiento a) {
        return new AlojamientoSearchResultDTO(
            a.getId(),
            a.getNombre(),         // Mapea 'nombre'
            a.getCiudad(),
            a.getTipo(),           // Mapea 'tipo'
            a.getPrecio(),         // Mapea 'precio'
            List.of(a.getFotoUrl()), // Mapea 'fotoUrl' a un array con una imagen
            a.getValoracionMedia(),// Mapea 'valoracionMedia'
            a.getCapacidad(),      // Mapea 'capacidad'
            a.getDistanciaCentro() // Mapea 'distanciaCentro' (Debes añadirlo a tu entidad)
        );
    }
}