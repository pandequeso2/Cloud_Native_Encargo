package cl.duoc.notas_service.service;

import cl.duoc.notas_service.client.IntegranteClient;
import cl.duoc.notas_service.dto.IntegranteDTO;
import cl.duoc.notas_service.dto.NotaEvento;
import cl.duoc.notas_service.dto.NotasIntegranteDTO;
import cl.duoc.notas_service.model.Nota;
import cl.duoc.notas_service.repository.NotaRepository;
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
    private KafkaTemplate<String, Object> kafkaTemplate;

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
            Integer idEstudiante = nota.getEstudianteId();
            String key = idEstudiante != null ? idEstudiante.toString() : "unknown";

            NotaEvento notaEvento = NotaEvento.builder()
                    .id(nota.getIdNota())
                    .idNota(nota.getIdNota())
                    .estudianteId(idEstudiante)
                    .idEstudiante(idEstudiante)
                    .idIntegrante(idEstudiante)
                    .asignatura(nota.getAsignatura() != null && !nota.getAsignatura().isBlank() ? nota.getAsignatura() : "General")
                    .calificacion(nota.getNota())
                    .nota(nota.getNota())
                    .fecha(LocalDateTime.now().toString())
                    .fechaCreacion(LocalDateTime.now().toString())
                    .idPonderacion(nota.getPonderacion() != null ? nota.getPonderacion().getIdPonderacion() : null)
                    .build();

            // Envío explícito a Kafka serializado como JSON
            kafkaTemplate.send(topicGradesInput, key, notaEvento);

            log.info("[KAFKA PRODUCER] Evento NotaEvento enviado a Kafka. Tópico: {}, Key (ID Estudiante): {}, Evento: {}",
                    topicGradesInput, key, notaEvento);
            System.out.println("[KAFKA PRODUCER] Publicado en '" + topicGradesInput + "' - Key: " + key + " - Calificación: " + nota.getNota());
        } catch (Exception e) {
            log.error("Error al publicar evento NotaEvento en Kafka para la nota ID: {}", nota.getIdNota(), e);
            System.err.println("[KAFKA ERROR] Error al publicar nota en Kafka: " + e.getMessage());
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
        IntegranteDTO integranteDTO = integranteClient.obtenerIntegrante(idIntegrante);
        List<Nota> notas = notaRepository.findByIdIntegrante(idIntegrante);

        NotasIntegranteDTO dto = new NotasIntegranteDTO();
        dto.setIntegrante(integranteDTO);
        dto.setNotas(notas);

        return dto;
    }
}
