package cl.duoc.notas_service.service;

import cl.duoc.notas_service.client.IntegranteClient;
import cl.duoc.notas_service.dto.IntegranteDTO;
import cl.duoc.notas_service.dto.NotasIntegranteDTO;
import cl.duoc.notas_service.model.Nota;
import cl.duoc.notas_service.repository.NotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotaService {

    @Autowired
    private NotaRepository notaRepository;

    @Autowired
    private IntegranteClient integranteClient;

    public List<Nota> obtenerNotas() {
        return notaRepository.findAll();
    }

    public Optional<Nota> buscarNotaPorId(Integer id) {
        return notaRepository.findById(id);
    }

    public Nota guardarNota(Nota nota) {
        return notaRepository.save(nota);
    }

    public Nota actualizarNota(Integer id, Nota nota) {
        if (notaRepository.existsById(id)) {

            nota.setIdNota(id);
            return notaRepository.save(nota);
        }
        throw new RuntimeException("No existe una nota con ID"+ id);
    }

    public void eliminarNota(Integer id) {
        if (notaRepository.existsById(id)) {
            notaRepository.deleteById(id);
        } else {
            throw new RuntimeException("No existe una nota con ID"+ id);
        }
    }

    public NotasIntegranteDTO obtenerNotasConIntegrante(Integer idIntegrante) {
        List<Nota> notas = notaRepository.findByIdIntegrante(idIntegrante);

        if (notas == null || notas.isEmpty()) {
            return null;
        }

        IntegranteDTO integrante = integranteClient.obtenerIntegrante(idIntegrante);

        NotasIntegranteDTO dto = new NotasIntegranteDTO();
        dto.setIntegrante(integrante);
        dto.setNotas(notas);

        return dto;
    }
}