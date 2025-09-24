package alquiler.negocio.service;

import alquiler.negocio.entity.Vivienda;
import java.util.List;

public interface ViviendaService_Interfaz {
    List<Vivienda> listarTodas();
    Vivienda guardar(Vivienda vivienda);
    Vivienda buscarPorId(Long id);
    void eliminar(Long id);
}
