package cl.duoc.entregas_service.service;

import cl.duoc.entregas_service.model.Entrega;
import cl.duoc.entregas_service.repository.EntregaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EntregaService {

    @Autowired
    private EntregaRepository repository;

    public List<Entrega> listarTodos() {
        return repository.findAll();
    }

    public Optional<Entrega> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public Entrega guardar(Entrega entrega) {
        return repository.save(entrega);
    }

    public Entrega actualizar(Integer id, Entrega entrega) {
        if (repository.existsById(id)) {
            entrega.setIdEntrega(id);
            return repository.save(entrega);
        }
        throw new RuntimeException("La entrega con ID " + id + " no existe.");
    }

    public void eliminar(Integer id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("La entrega con ID " + id + " no existe.");
        }
    }
}