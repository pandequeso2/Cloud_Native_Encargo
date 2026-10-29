package cl.duoc.kafka_service.service;

import cl.duoc.kafka_service.model.AlertaAcademica;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class AlertaService {

    private static final Logger log = LoggerFactory.getLogger(AlertaService.class);

    private final ObjectMapper objectMapper;
    private final ConcurrentLinkedDeque<AlertaAcademica> historialAlertas = new ConcurrentLinkedDeque<>();
    private static final int MAX_ALERTAS = 100;

    public AlertaService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.alerts-output:academic-alerts-output}",
            groupId = "academic-alerts-consumer-group"
    )
    public void consumirAlerta(String mensajeJson) {
        try {
            AlertaAcademica alerta = objectMapper.readValue(mensajeJson, AlertaAcademica.class);
            log.info("Consumer recibió AlertaAcademica en tiempo real: ID={}, Estudiante={}, Nota={}",
                    alerta.getIdAlerta(), alerta.getIdEstudiante(), alerta.getCalificacion());

            historialAlertas.addFirst(alerta);
            while (historialAlertas.size() > MAX_ALERTAS) {
                historialAlertas.removeLast();
            }
        } catch (Exception e) {
            log.error("Error al procesar mensaje de alerta desde tópico: {}", mensajeJson, e);
        }
    }

    public List<AlertaAcademica> obtenerAlertas() {
        return new ArrayList<>(historialAlertas);
    }

    public void limpiarAlertas() {
        historialAlertas.clear();
    }
}
