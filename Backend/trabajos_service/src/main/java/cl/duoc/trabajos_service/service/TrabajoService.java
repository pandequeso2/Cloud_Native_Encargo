package cl.duoc.trabajos_service.service;

import cl.duoc.trabajos_service.model.Trabajo;
import cl.duoc.trabajos_service.repository.TrabajoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrabajoService {

    @Autowired
    private TrabajoRepository trabajoRepository;

    public List<Trabajo> obtenerTrabajos() {
        return trabajoRepository.findAll();
    }

    public Optional<Trabajo> buscarTrabajoPorId(Integer id) {
        return trabajoRepository.findById(id);
    }

    public Trabajo guardarTrabajo(Trabajo trabajo) {
        return trabajoRepository.save(trabajo);
    }

    public Trabajo actualizarTrabajo(Integer id, Trabajo trabajo) {
        if (trabajoRepository.existsById(id)) {
            trabajo.setIdTrabajo(id);
            return trabajoRepository.save(trabajo);
        }
        throw new RuntimeException("No existe un trabajo con id "+ id);
    }

    public void eliminarTrabajo(Integer id) {
        if (trabajoRepository.existsById(id)) {
            trabajoRepository.deleteById(id);
        } else {
            throw new RuntimeException("No existe un trabajo con id " +id);
        }
    }

    public Trabajo obtenerPorGrupo(Integer idGrupo) {
        Trabajo trabajo = trabajoRepository.findByIdGrupo(idGrupo);
        if (trabajo == null) {
            throw new RuntimeException("No existe trabajo para el grupo " + idGrupo);
        }
        return trabajo;
    }
}