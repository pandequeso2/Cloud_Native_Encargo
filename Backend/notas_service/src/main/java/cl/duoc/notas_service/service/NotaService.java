package cl.duoc.notas_service.service;

import cl.duoc.notas_service.client.IntegranteClient;
import cl.duoc.notas_service.dto.IntegranteDTO;
import cl.duoc.notas_service.dto.NotaEvento;
import cl.duoc.notas_service.dto.NotasIntegranteDTO;
import cl.duoc.notas_service.model.Nota;
import cl.duoc.notas_service.repository.NotaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class NotaService {

    @Autowired
    private NotaRepository notaRepository;

    @Autowired
    private IntegranteClient integranteClient;

    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.kafka.topics.grades-input:academic-grades-input}")
    private String topicGradesInput = "academic-grades-input";

    public List<Nota> obtenerNotas() {
        return notaRepository.findAll();
    }

    public Optional<Nota> buscarNotaPorId(Integer id) {
        return notaRepository.findById(id);
    }

    public Nota guardarNota(Nota nota) {
        String asignatura = nota.getAsignatura();
        Nota guardada = notaRepository.save(nota);
        if (asignatura != null) {
            guardada.setAsignatura(asignatura);
        }

        // Publicar evento en Apache Kafka inmediatamente después de persistir
        enviarEventoKafka(guardada);

        return guardada;
    }

    private void enviarEventoKafka(Nota nota) {
        if (kafkaTemplate == null) {
            log.warn("KafkaTemplate no configurado o no disponible. No se envió el evento para nota ID: {}", nota.getIdNota());
            return;
        }

        try {
            Integer idEstudiante = nota.getIdIntegrante();
            String key = idEstudiante != null ? String.valueOf(idEstudiante) : "unknown";

            NotaEvento evento = NotaEvento.builder()
                    .idNota(nota.getIdNota())
                    .idEstudiante(idEstudiante)
                    .idIntegrante(idEstudiante)
                    .nota(nota.getNota())
                    .calificacion(nota.getNota())
                    .asignatura(nota.getAsignatura() != null && !nota.getAsignatura().isBlank() ? nota.getAsignatura() : "General")
                    .idPonderacion(nota.getPonderacion() != null ? nota.getPonderacion().getIdPonderacion() : null)
                    .fechaCreacion(LocalDateTime.now().toString())
                    .build();

            String jsonPayload = objectMapper.writeValueAsString(evento);
            kafkaTemplate.send(topicGradesInput, key, jsonPayload);
            log.info("Evento NotaEvento enviado exitosamente a Kafka. Tópico: {}, Key (ID Estudiante): {}, Evento: {}",
                    topicGradesInput, key, jsonPayload);
        } catch (Exception e) {
            log.error("Error al publicar evento NotaEvento en Kafka para la nota ID: {}", nota.getIdNota(), e);
        }
    }

    public Nota actualizarNota(Integer id, Nota nota) {
        if (notaRepository.existsById(id)) {
            nota.setIdNota(id);
            return notaRepository.save(nota);
        }
        throw new RuntimeException("No existe una nota con ID" + id);
    }

    public void eliminarNota(Integer id) {
        if (notaRepository.existsById(id)) {
            notaRepository.deleteById(id);
        } else {
            throw new RuntimeException("No existe una nota con ID" + id);
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
