package cl.duoc.profesores_service.service;

import cl.duoc.profesores_service.client.SeccionClient;
import cl.duoc.profesores_service.dto.ProfesorSeccionDTO;
import cl.duoc.profesores_service.dto.SeccionDTO;
import cl.duoc.profesores_service.model.Profesor;
import cl.duoc.profesores_service.repository.ProfesorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfesorService {

    @Autowired
    private ProfesorRepository profesorRepository;

    public List<Profesor> obtenerProfesores() {
        return profesorRepository.findAll();
    }

    public Optional<Profesor> buscarProfesorPorId(Integer id) {
        return profesorRepository.findById(id);
    }

    public Profesor guardarProfesor(Profesor profesor) {
        return profesorRepository.save(profesor);
    }

    public Profesor actualizarProfesor(Integer id, Profesor profesor) {
        if (profesorRepository.existsById(id)) {
            profesor.setIdProfesor(id);
            return profesorRepository.save(profesor);
        }
        throw new RuntimeException("No existe un profesor con id "+id);
    }

    public void eliminarProfesor(Integer id) {
        if (profesorRepository.existsById(id)) {
            profesorRepository.deleteById(id);
        } else {
            throw new RuntimeException("No existe un profesor con id " + id);
        }
    }

    @Autowired
    private SeccionClient seccionClient;

    public ProfesorSeccionDTO obtenerProfesorConSecciones(Integer idProfesor) {
        Profesor profesor = profesorRepository.findById(idProfesor)
                .orElseThrow(() -> new RuntimeException("No existe profesor"));

        List<SeccionDTO> secciones = seccionClient.obtenerPorProfesor(idProfesor);

        ProfesorSeccionDTO dto = new ProfesorSeccionDTO();
        dto.setIdProfesor(profesor.getIdProfesor());
        dto.setNombre(profesor.getNombreProfesor());
        dto.setApellido(profesor.getApellidoProfesor());
        dto.setCorreoElectronico(profesor.getCorreoElectronico());
        dto.setSecciones(secciones);

        return dto;
    }
}