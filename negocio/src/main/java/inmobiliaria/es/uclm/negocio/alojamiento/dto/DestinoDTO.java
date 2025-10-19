// Archivo: .../negocio/alojamiento/dto/DestinoDTO.java
package inmobiliaria.es.uclm.negocio.alojamiento.dto;

// Un "DTO" (Data Transfer Object) o "Record"
// Es una clase simple para transportar datos.
public class DestinoDTO {
    private String ciudad;
    private String fotoUrl;

    // Constructor
    public DestinoDTO(String ciudad, String fotoUrl) {
        this.ciudad = ciudad;
        this.fotoUrl = fotoUrl;
    }

    // Getters
    public String getCiudad() { return ciudad; }
    public String getFotoUrl() { return fotoUrl; }
}