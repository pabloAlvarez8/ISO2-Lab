package inmobiliaria.es.uclm.negocio.alojamiento.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DestinationDTOTest { // <--- El nombre de la clase acaba en Test
    @Test
    void record_WorksCorrectly() {
        var dto = new DestinoDTO("Madrid", "foto.jpg");
        assertEquals("Madrid", dto.ciudad());
    }
}