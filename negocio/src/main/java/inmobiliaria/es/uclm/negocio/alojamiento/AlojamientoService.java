package inmobiliaria.es.uclm.negocio.alojamiento;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlojamientoService implements AlojamientoService_Interfaz {

    private final AlojamientoRepository repo;

    // Inyección de dependencia del repository
    public AlojamientoService(AlojamientoRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Alojamiento> listarTodos() {
        return repo.findAll();
    }

    @Override
    public void guardar(Alojamiento alojamiento) {
        repo.save(alojamiento);
    }

    @Override
    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<Alojamiento> buscarPorCiudad(String ciudad) {
        return repo.findByCiudad(ciudad);
    }
}
