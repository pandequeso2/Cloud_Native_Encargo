package cl.duoc.notas_service.service;

import cl.duoc.notas_service.model.Nota;
import cl.duoc.notas_service.model.Ponderacion;
import cl.duoc.notas_service.repository.NotaRepository;
import cl.duoc.notas_service.repository.PonderacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PonderacionService {

    @Autowired
    private PonderacionRepository ponderacionRepository;

    public List<Ponderacion> obtenerPonderacion() {
        return ponderacionRepository.findAll();
    }

    public Optional<Ponderacion> buscarPonderacionPorId(Integer id) {
        return ponderacionRepository.findById(id);
    }

    public Ponderacion guardarPonderacion(Ponderacion ponderacion) {
        return ponderacionRepository.save(ponderacion);
    }

    public Ponderacion actualizarPonderacion(Integer id, Ponderacion ponderacion) {
        if (ponderacionRepository.existsById(id)) {

            ponderacion.setIdPonderacion(id);
            return ponderacionRepository.save(ponderacion);
        }
        throw new RuntimeException("No existe una ponderacion con ID" + id);
    }

    public void eliminarPonderacion(Integer id) {
        if (ponderacionRepository.existsById(id)) {
            ponderacionRepository.deleteById(id);
        } else {
            throw new RuntimeException("No existe una ponderacion con ID" + id);
        }
    }
}
