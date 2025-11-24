package inmobiliaria.es.uclm.negocio.alojamiento.dto;

import java.math.BigDecimal;
import java.util.List;
import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;

/**
 * DTO (Data Transfer Object) inmutable que proyecta los datos de un alojamiento
 * para los resultados de búsqueda.
 * <p>
 * Se implementa como un Java Record para garantizar inmutabilidad y reducir el boilerplate.
 * Su estructura está intencionalmente desacoplada de la entidad JPA {@link Alojamiento} y
 * adaptada al contrato JSON específico que espera el cliente web (script 'detalle.js').
 * Esto permite cambiar el modelo de datos interno sin romper la API pública.
 * </p>
 *
 * @param id Identificador único del recurso.
 * @param title Título comercial del anuncio. Renombrado desde 'nombre' para el frontend.
 * @param ciudad Ubicación geográfica.
 * @param type Categoría del inmueble. Renombrado desde 'tipo'.
 * @param price Precio por noche. Renombrado desde 'precio'.
 * @param images Lista de URLs de imágenes. El frontend requiere un array/galería, aunque la entidad base solo tenga una.
 * @param rating Puntuación media. Renombrado desde 'valoracionMedia'.
 * @param capacity Capacidad máxima de personas.
 * @param distance Distancia al punto de interés (centro).
 */
public record AlojamientoSearchResultDTO(
    int id,
    String title,
    String ciudad,
    String type,
    BigDecimal price,
    List<String> images,
    Double rating,
    int capacity,
    BigDecimal distance
) {

    /**
     * Método factoría estático para convertir la entidad de persistencia en este DTO.
     * <p>
     * Centraliza la lógica de transformación y mapeo de campos, evitando que esta lógica
     * se disperse por el controlador o el servicio. Realiza adaptaciones de tipos,
     * como envolver la URL de la imagen única en una {@link List} para cumplir con la
     * estructura de galería que espera la vista.
     * </p>
     *
     * @param a Entidad {@link Alojamiento} con los datos origen.
     * @return Instancia del DTO lista para ser serializada y enviada al cliente.
     */
    public static AlojamientoSearchResultDTO fromEntity(Alojamiento a) {
        return new AlojamientoSearchResultDTO(
            a.getId(),
            a.getNombre(),         
            a.getCiudad(),
            a.getTipo(),           
            a.getPrecio(),         
            // Adaptación estructural: Convertimos el String único en una Lista
            // para mantener la compatibilidad con componentes de galería en el UI.
            List.of(a.getFotoUrl()), 
            a.getValoracionMedia(),
            a.getCapacidad(),      
            a.getDistanciaCentro() 
        );
    }
}