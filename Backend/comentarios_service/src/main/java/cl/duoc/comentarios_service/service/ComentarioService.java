package cl.duoc.comentarios_service.service;

import cl.duoc.comentarios_service.model.Comentario;
import cl.duoc.comentarios_service.repository.ComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository repository;

    public List<Comentario> listarTodos() {
        return repository.findAll();
    }

    public Optional<Comentario> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public Comentario guardar(Comentario comentario) {
        return repository.save(comentario);
    }

    public Comentario actualizar(Integer id, Comentario comentario) {
        if (repository.existsById(id)) {
            comentario.setIdComentario(id);
            return repository.save(comentario);
        }
        throw new RuntimeException("Comentario no encontrado");
    }

    public void eliminar(Integer id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Comentario no encontrado");
        }
    }
}