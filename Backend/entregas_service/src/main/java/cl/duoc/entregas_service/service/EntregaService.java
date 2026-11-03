package cl.duoc.entregas_service.service;

import cl.duoc.entregas_service.dto.EntregaEvento;
import cl.duoc.entregas_service.model.Entrega;
import cl.duoc.entregas_service.repository.EntregaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EntregaService {

    public static final String DEFAULT_EXCHANGE = "orders.exchange";
    public static final String DEFAULT_ROUTING_KEY = "order.created";

    @Autowired
    private EntregaRepository repository;

    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    @Autowired(required = false)
    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.rabbitmq.exchange:orders.exchange}")
    private String exchange = DEFAULT_EXCHANGE;

    @Value("${app.rabbitmq.routing-key:order.created}")
    private String routingKey = DEFAULT_ROUTING_KEY;

    public List<Entrega> listarTodos() {
        return repository.findAll();
    }

    public Optional<Entrega> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public Entrega guardar(Entrega entrega) {
        String estudiante = entrega.getEstudiante();
        Entrega guardada = repository.save(entrega);
        if (estudiante != null) {
            guardada.setEstudiante(estudiante);
        }

        enviarEventoRabbitMQ(guardada, "CREADA");
        return guardada;
    }

    public Entrega actualizar(Integer id, Entrega entrega) {
        if (repository.existsById(id)) {
            String estudiante = entrega.getEstudiante();
            entrega.setIdEntrega(id);
            Entrega actualizada = repository.save(entrega);
            if (estudiante != null) {
                actualizada.setEstudiante(estudiante);
            }

            enviarEventoRabbitMQ(actualizada, "ACTUALIZADA");
            return actualizada;
        }
        throw new RuntimeException("La entrega con ID " + id + " no existe.");
    }

    private void enviarEventoRabbitMQ(Entrega entrega, String accion) {
        if (rabbitTemplate == null) {
            log.warn("RabbitTemplate no configurado o no disponible. No se envió el evento para entrega ID: {}", entrega.getIdEntrega());
            return;
        }

        try {
            String estudiante = (entrega.getEstudiante() != null && !entrega.getEstudiante().isBlank())
                    ? entrega.getEstudiante()
                    : (entrega.getIdGrupo() != null ? "Grupo-" + entrega.getIdGrupo() : "Estudiante Duoc");

            String fecha = entrega.getFechaEntrega() != null
                    ? entrega.getFechaEntrega().toString()
                    : LocalDate.now().toString();

            EntregaEvento evento = EntregaEvento.builder()
                    .idEntrega(entrega.getIdEntrega())
                    .estudiante(estudiante)
                    .idGrupo(entrega.getIdGrupo())
                    .idTrabajo(entrega.getIdTrabajo())
                    .estado(entrega.getEstado() != null ? entrega.getEstado().name() : "PENDIENTE")
                    .fecha(fecha)
                    .accion(accion)
                    .build();

            ObjectMapper mapper = objectMapper != null ? objectMapper : new ObjectMapper();
            String jsonPayload = mapper.writeValueAsString(evento);

            rabbitTemplate.convertAndSend(exchange, routingKey, jsonPayload);
            log.info("Evento EntregaEvento enviado exitosamente a RabbitMQ. Exchange: {}, RoutingKey: {}, Payload: {}",
                    exchange, routingKey, jsonPayload);
        } catch (Exception e) {
            log.error("Error al publicar evento en RabbitMQ para la entrega ID: {}", entrega.getIdEntrega(), e);
        }
    }

    public void eliminar(Integer id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("La entrega con ID " + id + " no existe.");
        }
    }
}
