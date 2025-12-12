package inmobiliaria.es.uclm.negocio.alojamiento.dto;

import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import java.math.BigDecimal;
import java.util.List;

public record AlojamientoSearchResultDTO(
        Long id,
        String title,         // El JS espera 'title' -> Mapeamos desde 'nombre'
        String ciudad,        // El JS espera 'ciudad'
        String type,          // El JS espera 'type'  -> Mapeamos desde 'tipo'
        BigDecimal price,     // El JS espera 'price' -> Mapeamos desde 'precio'
        List<String> images,  // El JS espera 'images' (Array) -> Mapeamos desde 'fotoUrl'
        Double rating,        // El JS espera 'rating' -> Mapeamos desde 'valoracionMedia'
        int capacity,         // El JS espera 'capacity' -> Mapeamos desde 'capacidad'
        BigDecimal distance   // El JS espera 'distance' -> Mapeamos desde 'distanciaCentro'
) {

    public static AlojamientoSearchResultDTO fromEntity(Alojamiento a) {

        // 1. PROTECCIÓN DE IMAGEN: Si no hay foto, ponemos una por defecto para que no rompa el frontend
        String fotoSegura = (a.getFotoUrl() != null && !a.getFotoUrl().isEmpty())
                ? a.getFotoUrl()
                : "/images/no-image.png";

        // 2. PROTECCIÓN DE DISTANCIA: Si es nula, ponemos 0
        BigDecimal distanciaSegura = (a.getDistanciaCentro() != null)
                ? a.getDistanciaCentro()
                : BigDecimal.ZERO;

        // 3. PROTECCIÓN DE VALORACIÓN: Si es nula, ponemos 0.0
        Double valoracionSegura = (a.getValoracionMedia() != null)
                ? a.getValoracionMedia()
                : 0.0;

        return new AlojamientoSearchResultDTO(
                a.getId(),
                a.getNombre(),         // <--- CORREGIDO: Usamos getNombre() porque así está en tu Entidad
                a.getCiudad(),
                a.getTipo(),           // <--- Usamos getTipo()
                a.getPrecio(),         // <--- Usamos getPrecio()
                List.of(fotoSegura),   // <--- Convertimos el String único en una Lista
                valoracionSegura,
                a.getCapacidad(),
                distanciaSegura        // <--- Usamos getDistanciaCentro()
        );
    }
}