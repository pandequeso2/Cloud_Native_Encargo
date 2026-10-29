package cl.duoc.kafka_service.controller;

import cl.duoc.kafka_service.model.AlertaAcademica;
import cl.duoc.kafka_service.model.NotaEvento;
import cl.duoc.kafka_service.service.AlertaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Kafka Streaming", description = "Endpoints para interactuar y probar el pipeline de Kafka Streams")
@RestController
@RequestMapping("/api/v1/kafka")
public class KafkaController {

    private static final Logger log = LoggerFactory.getLogger(KafkaController.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AlertaService alertaService;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.grades-input:academic-grades-input}")
    private String gradesInputTopic;

    @Value("${app.kafka.topics.alerts-output:academic-alerts-output}")
    private String alertsOutputTopic;

    @Value("${spring.kafka.streams.application-id:academic-pipeline-app}")
    private String applicationId;

    public KafkaController(KafkaTemplate<String, String> kafkaTemplate,
                           AlertaService alertaService,
                           ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.alertaService = alertaService;
        this.objectMapper = objectMapper;
    }

    @Operation(summary = "Publica una nota en Kafka", description = "Envía un evento NotaEvento al tópico academic-grades-input con Key = ID del estudiante para testing del pipeline")
    @PostMapping("/publicar-nota")
    public ResponseEntity<Map<String, Object>> publicarNota(@RequestBody NotaEvento evento) {
        try {
            if (evento.getFechaCreacion() == null) {
                evento.setFechaCreacion(LocalDateTime.now().toString());
            }

            Integer estudianteId = evento.obtenerEstudianteId();
            String key = estudianteId != null ? String.valueOf(estudianteId) : "unknown";
            String jsonPayload = objectMapper.writeValueAsString(evento);

            kafkaTemplate.send(gradesInputTopic, key, jsonPayload);
            log.info("Prueba: NotaEvento enviado manualmente al tópico '{}'. Key: {}, Payload: {}",
                    gradesInputTopic, key, jsonPayload);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("mensaje", "Evento enviado al tópico de entrada");
            response.put("topico", gradesInputTopic);
            response.put("key", key);
            response.put("evento", evento);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al publicar nota de prueba en Kafka", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("mensaje", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @Operation(summary = "Obtiene las alertas detectadas", description = "Retorna el historial de alertas emitidas por la topología de Kafka Streams a academic-alerts-output")
    @GetMapping("/alertas")
    public ResponseEntity<List<AlertaAcademica>> obtenerAlertas() {
        return ResponseEntity.ok(alertaService.obtenerAlertas());
    }

    @Operation(summary = "Limpia las alertas", description = "Elimina las alertas acumuladas en memoria")
    @DeleteMapping("/alertas")
    public ResponseEntity<Map<String, String>> limpiarAlertas() {
        alertaService.limpiarAlertas();
        Map<String, String> resp = new HashMap<>();
        resp.put("mensaje", "Historial de alertas limpiado");
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Estado del pipeline", description = "Retorna la configuración y estado actual del pipeline de streaming")
    @GetMapping("/estado")
    public ResponseEntity<Map<String, Object>> estadoPipeline() {
        Map<String, Object> info = new HashMap<>();
        info.put("applicationId", applicationId);
        info.put("inputTopic", gradesInputTopic);
        info.put("outputTopic", alertsOutputTopic);
        info.put("alertasDetectadas", alertaService.obtenerAlertas().size());
        info.put("status", "ACTIVE");
        return ResponseEntity.ok(info);
    }
}
