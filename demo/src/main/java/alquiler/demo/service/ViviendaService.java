package alquiler.demo.service;

import alquiler.demo.entity.Vivienda;
import java.util.List;

public interface ViviendaService {
    List<Vivienda> listarTodas();
    Vivienda guardar(Vivienda vivienda);
    Vivienda buscarPorId(Long id);
    void eliminar(Long id);
}
