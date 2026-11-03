package cl.duoc.kafka_service.controller;

import cl.duoc.kafka_service.service.SlowConsumer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Controlador de Pruebas de Carga y Rendimiento en Apache Kafka.
 * Genera ráfagas masivas en paralelo aprovechando el tuning del productor (32KB batch, 20ms linger, snappy)
 * e induciendo intencionalmente Consumer Lag contra el SlowConsumer (grupo: analytics-group).
 */
@Tag(name = "Kafka Load Testing & Tuning", description = "Endpoints para pruebas de carga masiva, medición de throughput y generación de Consumer Lag")
@RestController
@RequestMapping("/api/v1/kafka/test")
public class LoadTestController {

    private static final Logger log = LoggerFactory.getLogger(LoadTestController.class);
    private static final String SENSOR_TOPIC = "sensor-topic";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SlowConsumer slowConsumer;

    public LoadTestController(KafkaTemplate<String, String> kafkaTemplate, SlowConsumer slowConsumer) {
        this.kafkaTemplate = kafkaTemplate;
        this.slowConsumer = slowConsumer;
    }

    @Operation(
            summary = "Inicia prueba de carga masiva",
            description = "Envía una ráfaga masiva de mensajes paralelos al tópico sensor-topic usando el productor con tuning (32KB batch, 20ms linger, compresión snappy) y calcula el throughput."
    )
    @GetMapping("/start")
    public ResponseEntity<Map<String, Object>> startLoadTest(
            @Parameter(description = "Cantidad de mensajes masivos a emitir", example = "10000")
            @RequestParam(value = "count", defaultValue = "10000") int count) {

        if (count <= 0) {
            count = 10000;
        }

        log.info("Iniciando prueba de carga masiva: enviando {} mensajes a '{}'...", count, SENSOR_TOPIC);
        long startTime = System.currentTimeMillis();

        // Envío masivo en paralelo aprovechando IntStream.parallel()
        IntStream.range(0, count).parallel().forEach(i -> {
            String key = "sensor-" + (i % 10);
            String payload = String.format("{\"id\":%d,\"sensorId\":\"%s\",\"temperatura\":%.2f,\"timestamp\":%d}",
                    i, key, 20.0 + (i % 15), System.currentTimeMillis());
            kafkaTemplate.send(SENSOR_TOPIC, key, payload);
        });

        // Asegurar vaciado (flush) completo de los buffers del productor
        kafkaTemplate.flush();

        long endTime = System.currentTimeMillis();
        long executionTimeMs = Math.max(1, endTime - startTime);
        double executionTimeSec = executionTimeMs / 1000.0;
        double throughputMsgsSec = count / executionTimeSec;

        log.info("Prueba completada: {} mensajes en {} ms (Throughput: {} msgs/sec)",
                count, executionTimeMs, String.format("%.2f", throughputMsgsSec));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "SUCCESS");
        result.put("topic", SENSOR_TOPIC);
        result.put("messagesSent", count);
        result.put("executionTimeMs", executionTimeMs);
        result.put("executionTimeSec", executionTimeSec);
        result.put("throughputMsgsSec", Math.round(throughputMsgsSec * 100.0) / 100.0);
        result.put("producerTuning", Map.of(
                "batchSize", "32768 bytes (32 KB)",
                "lingerMs", 20,
                "compressionType", "snappy"
        ));
        result.put("consumerSimulation", Map.of(
                "groupId", "analytics-group",
                "simulatedLatencyPerMessage", "10 ms",
                "consumerLagExpected", "Verificable en Kafka UI (puerto 8080)"
        ));

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Obtiene estado del SlowConsumer", description = "Retorna la cantidad de mensajes consumidos hasta el momento por analytics-group")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("topic", SENSOR_TOPIC);
        status.put("slowConsumerGroupId", "analytics-group");
        status.put("messagesProcessedBySlowConsumer", slowConsumer.getProcessedCount());
        return ResponseEntity.ok(status);
    }

    @Operation(summary = "Reinicia contador del SlowConsumer", description = "Pone a cero el contador en memoria del consumidor lento")
    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetConsumerCounter() {
        slowConsumer.resetProcessedCount();
        return ResponseEntity.ok(Map.of("mensaje", "Contador de SlowConsumer reiniciado"));
    }
}

