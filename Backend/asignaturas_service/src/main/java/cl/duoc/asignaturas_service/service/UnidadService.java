package cl.duoc.asignaturas_service.service;

import cl.duoc.asignaturas_service.dto.UnidadDTO;
import cl.duoc.asignaturas_service.model.Unidad;
import cl.duoc.asignaturas_service.repository.UnidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UnidadService {

    public List<Unidad> obtenerUnidades(){ return unidadRepository.findAll();}

    public Unidad guardarUnidad (Unidad unidad){return unidadRepository.save(unidad);}

    public Optional<Unidad> buscarUnidadPorId(Integer id){ return unidadRepository.findById(id);}

    public Unidad actualizarUnidades (Integer id, Unidad unidad){
        if (unidadRepository.existsById(id)){
            unidad.setIdUnidad(id);
            return unidadRepository.save(unidad);
        }
        throw new RuntimeException("La unidad con ID " + id + " no existe");
    }

    public void eliminarUnidad(Integer id) {
        if (unidadRepository.existsById(id)) {
            unidadRepository.deleteById(id);
        } else {
            throw new RuntimeException("No existe una unidad con ID " + id);
        }
    }

    @Autowired
    private UnidadRepository unidadRepository;

}
