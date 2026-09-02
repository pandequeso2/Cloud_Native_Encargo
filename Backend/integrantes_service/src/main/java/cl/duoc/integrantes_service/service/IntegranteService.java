package cl.duoc.integrantes_service.service;

import cl.duoc.integrantes_service.client.RolClient;
import cl.duoc.integrantes_service.dto.IntegranteRolDTO;
import cl.duoc.integrantes_service.dto.RolDTO;
import cl.duoc.integrantes_service.model.Integrante;
import cl.duoc.integrantes_service.repository.IntegranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IntegranteService {

    @Autowired
    private IntegranteRepository integranteRepository;

    public List<Integrante> obtenerIntegrantes() {
        return integranteRepository.findAll();
    }

    public Optional<Integrante> buscarIntegrantePorId(Integer id) {
        return integranteRepository.findById(id);
    }

    public Integrante guardarIntegrante(Integrante integrante) {
        return integranteRepository.save(integrante);
    }

    public Integrante actualizarIntegrante(Integer id, Integrante integrante) {
        if (integranteRepository.existsById(id)) {

            integrante.setIdIntegrante(id);
            return integranteRepository.save(integrante);
        }
        throw new RuntimeException("El integrante con ID " + id + " no existe.");
    }

    public void eliminarIntegrante(Integer id) {
        if (integranteRepository.existsById(id)) {
            integranteRepository.deleteById(id);
        } else {
            throw new RuntimeException("No existe un integrante con ID "+ id);
        }
    }

    public List<Integrante> obtenerPorGrupo(Integer idGrupo) {
        return integranteRepository.findByIdGrupo(idGrupo);
    }

    @Autowired
    private RolClient rolClient;

    public IntegranteRolDTO obtenerIntegranteConRol(Integer idIntegrante) {
        Integrante integrante = integranteRepository.findById(idIntegrante)
                .orElseThrow(() -> new RuntimeException("No existe ese integrante"));

        RolDTO rol = rolClient.obtenerRol(integrante.getIdRol());

        IntegranteRolDTO dto = new IntegranteRolDTO();
        dto.setIdIntegrante(integrante.getIdIntegrante());
        dto.setNombre(integrante.getNombre());
        dto.setApellido(integrante.getApellido());
        dto.setCorreoElectronico(integrante.getCorreoElectronico());
        dto.setRol(rol);

        return dto;
    }
}
