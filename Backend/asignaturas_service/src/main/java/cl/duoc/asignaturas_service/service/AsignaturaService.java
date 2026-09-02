package cl.duoc.asignaturas_service.service;

import cl.duoc.asignaturas_service.client.AsignaturaClient;
import cl.duoc.asignaturas_service.client.SeccionClient;
import cl.duoc.asignaturas_service.dto.AsignaturaDTO;
import cl.duoc.asignaturas_service.dto.AsignaturaSeccionDTO;
import cl.duoc.asignaturas_service.dto.SeccionDTO;
import cl.duoc.asignaturas_service.model.Asignatura;
import cl.duoc.asignaturas_service.repository.AsignaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AsignaturaService {

    @Autowired
    private AsignaturaRepository asignaturaRepository;

    @Autowired
    private SeccionClient seccionClient;

    public List<Asignatura> obtenerAsignaturas() {
        return asignaturaRepository.findAll();
    }

    public Optional<Asignatura> buscarAsignaturaPorId(Integer id) {
        return asignaturaRepository.findById(id);
    }

    public Asignatura guardarAsignatura(Asignatura asignatura) {
        return asignaturaRepository.save(asignatura);
    }

    public Asignatura actualizarAsignatura(Integer id, Asignatura asignatura) {
        if (asignaturaRepository.existsById(id)) {
            asignatura.setIdAsignatura(id);
            return asignaturaRepository.save(asignatura);
        }
        throw new RuntimeException("La asignatura con ID " + id + " no existe");
    }

    public void eliminarAsignatura(Integer id) {
        if (asignaturaRepository.existsById(id)) {
            asignaturaRepository.deleteById(id);
        } else {
            throw new RuntimeException("La asignatura con ID " + id + " no existe");
        }
    }



    public AsignaturaSeccionDTO obtenerAsignaturaConSecciones(Integer idAsignatura) {
        Asignatura asignatura = asignaturaRepository.findById(idAsignatura)
                .orElseThrow(() -> new RuntimeException("Asignatura no encontrada"));

        List<SeccionDTO> secciones = seccionClient.obtenerPorAsignatura(idAsignatura);

        AsignaturaSeccionDTO dto = new AsignaturaSeccionDTO();
        dto.setIdAsignatura(asignatura.getIdAsignatura());
        dto.setNombreAsignatura(asignatura.getNombreAsignatura());
        dto.setIdProfesor(asignatura.getIdProfesor());
        dto.setSecciones(secciones);

        return dto;
    }

}