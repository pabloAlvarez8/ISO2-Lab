package inmobiliaria.es.uclm.negocio.alojamiento.dto;

/**
 * Objeto de Transferencia de Datos (DTO) que representa la información visual de un destino turístico.
 * <p>
 * Esta clase se utiliza para transportar datos ligeros (nombre y foto) desde la capa de negocio
 * hacia la interfaz de usuario, habitualmente para poblar componentes como carruseles de
 * "Ciudades Destacadas" o sugerencias de búsqueda, sin exponer la lógica completa de las entidades.
 * </p>
 */
public class DestinoDTO {

    /**
     * Nombre de la ciudad o localidad del destino.
     */
    private String ciudad;

    /**
     * URL o ruta relativa de la imagen representativa del destino.
     */
    private String fotoUrl;

    /**
     * Constructor para inicializar el DTO con datos inmutables para la vista.
     *
     * @param ciudad Nombre de la ciudad.
     * @param fotoUrl URL de la imagen asociada.
     */
    public DestinoDTO(String ciudad, String fotoUrl) {
        this.ciudad = ciudad;
        this.fotoUrl = fotoUrl;
    }

    /**
     * Obtiene el nombre de la ciudad.
     * @return Cadena con el nombre del destino.
     */
    public String getCiudad() { 
        return ciudad; 
    }

    /**
     * Obtiene la referencia a la imagen.
     * @return Cadena con la URL de la foto.
     */
    public String getFotoUrl() { 
        return fotoUrl; 
    }
}