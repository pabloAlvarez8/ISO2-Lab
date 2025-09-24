package alquiler.negocio.service;

import java.util.List;
import org.springframework.stereotype.Service;
import alquiler.negocio.entity.Vivienda;
import alquiler.negocio.repository.ViviendaRepository;

@Service
public class ViviendaService{

    private final ViviendaRepository repo;

    public ViviendaService(ViviendaRepository repo) {
        this.repo = repo;
    }

    public List<Vivienda> listarViviendas() {
        return repo.findAll();
    }

    public Vivienda obtenerVivienda(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Vivienda alquilarVivienda(Long id) {
        Vivienda v = repo.findById(id).orElse(null);
        if (v != null && v.isDisponible()) {
            v.setDisponible(false);
            repo.save(v);
        }
        return v;
    }
}
