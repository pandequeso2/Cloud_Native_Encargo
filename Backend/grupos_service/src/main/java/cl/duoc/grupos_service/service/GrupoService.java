package cl.duoc.grupos_service.service;

import cl.duoc.grupos_service.client.IntegranteClient;
import cl.duoc.grupos_service.client.TrabajoClient;
import cl.duoc.grupos_service.dto.GrupoIntegranteDTO;
import cl.duoc.grupos_service.dto.GrupoTrabajoDTO;
import cl.duoc.grupos_service.dto.IntegranteDTO;
import cl.duoc.grupos_service.dto.TrabajoDTO;
import cl.duoc.grupos_service.model.Grupo;
import cl.duoc.grupos_service.repository.GrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;
    @Autowired
    private IntegranteClient integranteClient;

    public List<Grupo> obtenerGrupos() {
        return grupoRepository.findAll();
    }

    public Optional<Grupo> buscarGrupoPorId(Integer id) {
        return grupoRepository.findById(id);
    }

    public Grupo guardarGrupo(Grupo grupo) {
        return grupoRepository.save(grupo);
    }

    public Grupo actualizarGrupo(Integer id, Grupo grupo) {
        if (grupoRepository.existsById(id)) {

            grupo.setIdGrupo(id);
            return grupoRepository.save(grupo);
        }
        throw new RuntimeException("La nota con ID " + id + " no existe.");
    }

    public void eliminarGrupo(Integer id) {
        if (grupoRepository.existsById(id)) {
            grupoRepository.deleteById(id);
        } else {
            throw new RuntimeException("No existe un grupo con ID " + id);
        }
    }

    public GrupoIntegranteDTO obtenerGrupoConIntegrantes(Integer idGrupo) {

        Grupo grupo = grupoRepository.findById(idGrupo).orElse(null);

        if (grupo == null) {
            return null;
        }

        List<IntegranteDTO> integrantes = integranteClient.obtenerPorGrupo(idGrupo);

        GrupoIntegranteDTO dto = new GrupoIntegranteDTO();
        dto.setIdGrupo(grupo.getIdGrupo());
        dto.setNombreGrupo(grupo.getNombreGrupo());
        dto.setGrupoLleno(grupo.getGrupoLleno());
        dto.setIntegrantes(integrantes);

        return dto;
    }

    @Autowired
    private TrabajoClient trabajoClient;

    public GrupoTrabajoDTO obtenerGrupoConTrabajo(Integer idGrupo) {
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new RuntimeException("El grupo no existe"));

        TrabajoDTO trabajo = trabajoClient.obtenerPorGrupo(idGrupo);

        GrupoTrabajoDTO dto = new GrupoTrabajoDTO();
        dto.setIdGrupo(grupo.getIdGrupo());
        dto.setNombreGrupo(grupo.getNombreGrupo());
        dto.setGrupoLleno(grupo.getGrupoLleno());
        dto.setTrabajo(trabajo);

        return dto;
    }
}
