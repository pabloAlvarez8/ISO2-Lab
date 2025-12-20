package inmobiliaria.es.uclm.negocio.alojamiento.dto;

// Al ser un record, Java crea automáticamente constructor, getters, equals y hashCode.
public record DestinoDTO(String ciudad, String fotoUrl) {}