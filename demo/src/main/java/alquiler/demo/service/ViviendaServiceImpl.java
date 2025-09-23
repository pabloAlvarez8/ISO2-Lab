package alquiler.demo.service;

import alquiler.demo.entity.Vivienda;
import alquiler.demo.repository.ViviendaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViviendaServiceImpl implements ViviendaService {

    private final ViviendaRepository viviendaRepository;

    public ViviendaServiceImpl(ViviendaRepository viviendaRepository) {
        this.viviendaRepository = viviendaRepository;
    }

    @Override
    public List<Vivienda> listarTodas() {
        return viviendaRepository.findAll();
    }

    @Override
    public Vivienda guardar(Vivienda vivienda) {
        return viviendaRepository.save(vivienda);
    }

    @Override
    public Vivienda buscarPorId(Long id) {
        return viviendaRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminar(Long id) {
        viviendaRepository.deleteById(id);
    }
}
