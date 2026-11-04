package cl.duoc.comentarios_service.service;

import cl.duoc.comentarios_service.dto.ComentarioEvento;
import cl.duoc.comentarios_service.model.Comentario;
import cl.duoc.comentarios_service.repository.ComentarioRepository;
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
public class ComentarioService {

    public static final String DEFAULT_TOPIC = "comentarios-academicos-topic";

    @Autowired
    private ComentarioRepository repository;

    @Autowired(required = false)
    private KafkaTemplate<String, ComentarioEvento> kafkaTemplate;

    @Value("${app.kafka.topics.comentarios:comentarios-academicos-topic}")
    private String topicComentarios = DEFAULT_TOPIC;

    public List<Comentario> listarTodos() {
        return repository.findAll();
    }

    public Optional<Comentario> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public Comentario guardar(Comentario comentario) {
        if (comentario.getFechaComentario() == null) {
            comentario.setFechaComentario(LocalDateTime.now());
        }
        if (comentario.getTipoComentario() == null) {
            comentario.setTipoComentario(Comentario.TipoComentario.CONSULTA);
        }
        if (comentario.getIdProfesor() == null) {
            comentario.setIdProfesor(1);
        }
        if (comentario.getIdEntrega() == null) {
            comentario.setIdEntrega(1);
        }

        Comentario guardado = repository.save(comentario);

        // Publicar evento en Kafka al crear un nuevo comentario usando entregaId como Key
        publicarEventoKafka(guardado);

        return guardado;
    }

    private void publicarEventoKafka(Comentario comentario) {
        if (kafkaTemplate == null) {
            log.warn("KafkaTemplate no disponible o no configurado. No se publica ComentarioEvento para comentario ID: {}", comentario.getIdComentario());
            return;
        }

        try {
            Integer entregaId = comentario.getIdEntrega();
            String key = entregaId != null ? entregaId.toString() : "unknown";

            ComentarioEvento evento = ComentarioEvento.builder()
                    .id(comentario.getIdComentario())
                    .entregaId(entregaId)
                    .usuarioId(comentario.getIdProfesor())
                    .textoComentario(comentario.getContenido())
                    .fechaCreacion(comentario.getFechaComentario() != null
                            ? comentario.getFechaComentario().toString()
                            : LocalDateTime.now().toString())
                    .tipoComentario(comentario.getTipoComentario() != null ? comentario.getTipoComentario().name() : null)
                    .build();

            kafkaTemplate.send(topicComentarios, key, evento);

            log.info("[KAFKA PRODUCER - comentarios_service] Evento ComentarioEvento publicado exitosamente en el tópico '{}'. Key (entregaId): {}, Payload: {}",
                    topicComentarios, key, evento);
            System.out.println("[KAFKA PRODUCER] Comentario publicado en tópico '" + topicComentarios + "' con Key (idEntrega): " + key);
        } catch (Exception e) {
            log.error("[KAFKA PRODUCER - comentarios_service] Error al publicar ComentarioEvento en Kafka para ID: {}", comentario.getIdComentario(), e);
            System.err.println("[KAFKA ERROR] Error al publicar comentario en Kafka: " + e.getMessage());
        }
    }

    public Comentario actualizar(Integer id, Comentario comentario) {
        if (repository.existsById(id)) {
            comentario.setIdComentario(id);
            if (comentario.getFechaComentario() == null) {
                comentario.setFechaComentario(LocalDateTime.now());
            }
            if (comentario.getTipoComentario() == null) {
                comentario.setTipoComentario(Comentario.TipoComentario.CONSULTA);
            }
            return repository.save(comentario);
        }
        throw new RuntimeException("Comentario no encontrado");
    }

    public void eliminar(Integer id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Comentario no encontrado");
        }
    }
}
