package cl.duoc.roles_service.service;

import cl.duoc.roles_service.model.Rol;
import cl.duoc.roles_service.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public List<Rol> listarTodos() {
        return rolRepository.findAll();
    }

    public Optional<Rol> buscarPorId(Integer id) {
        return rolRepository.findById(id);
    }

    public Rol guardar(Rol rol) {
        return rolRepository.save(rol);
    }

    public Rol actualizar(Integer id, Rol rol) {
        if (rolRepository.existsById(id)) {

            rol.setIdRol(id);
            return rolRepository.save(rol);
        }
        throw new RuntimeException("No existe un rol con id " +id);
    }

    public void eliminarRol(Integer id) {
        if (rolRepository.existsById(id)) {
            rolRepository.deleteById(id);
        } else {
            throw new RuntimeException("El rol con ID " + id + " no existe.");
        }
    }

    public Optional<Rol> buscarRolPorId(Integer id) {
        return rolRepository.findById(id);
    }

}
