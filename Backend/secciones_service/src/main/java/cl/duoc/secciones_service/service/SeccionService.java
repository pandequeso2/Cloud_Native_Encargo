package cl.duoc.secciones_service.service;

import cl.duoc.secciones_service.client.IntegranteClient;
import cl.duoc.secciones_service.dto.IntegranteDTO;
import cl.duoc.secciones_service.dto.SeccionIntegranteDTO;
import cl.duoc.secciones_service.model.Seccion;
import cl.duoc.secciones_service.repository.SeccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeccionService {

    @Autowired
    private SeccionRepository seccionRepository;
    @Autowired
    private IntegranteClient integranteClient;

    public List<Seccion> obtenerSecciones() { return seccionRepository.findAll();}

    public Optional<Seccion> buscarSeccionPorId(Integer id) {
        return seccionRepository.findById(id);
    }

    public Seccion guardarSeccion(Seccion seccion) {
        return seccionRepository.save(seccion);
    }

    public Seccion actualizarSeccion(Integer id, Seccion seccion) {
        if (seccionRepository.existsById(id)) {
            seccion.setIdSeccion(id);
            return seccionRepository.save(seccion);
        }
        throw new RuntimeException("La seccion con ID " + id + " no existe");
    }

    public void eliminarSeccion(Integer id) {
        if (seccionRepository.existsById(id)) {
            seccionRepository.deleteById(id);
        } else {
            throw new RuntimeException("La sección con ID " + id + " no existe.");
        }
    }

    public SeccionIntegranteDTO obtenerSeccionConIntegrante(Integer idSeccion, Integer idIntegrante){

        Seccion seccion = seccionRepository.findById(idSeccion).orElse(null);

        if (seccion==null){
            return null;
        }

        IntegranteDTO integrante = integranteClient.obtenerIntegrante(idIntegrante);

        SeccionIntegranteDTO dto = new SeccionIntegranteDTO();
        dto.setIdSeccion(seccion.getIdSeccion());
        dto.setCodigoSeccion(seccion.getCodigoSeccion());

        dto.setIntegrante(integrante);

        return dto;
    }

    public List<Seccion> obtenerPorAsignatura(Integer idAsignatura) {
        return seccionRepository.findByIdAsignatura(idAsignatura);
    }

    public List<Seccion> obtenerPorProfesor(Integer idProfesor) {
        return seccionRepository.findByIdProfesor(idProfesor);
    }
}
