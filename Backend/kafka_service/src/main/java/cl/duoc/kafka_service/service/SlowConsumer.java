package cl.duoc.kafka_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Consumidor Lento diseñado para simular latencia de procesamiento y Consumer Lag en Kafka.
 * Suscrito al tópico "sensor-topic" bajo el grupo "analytics-group".
 * Al introducir una pausa intencional de 10ms por mensaje frente a una ráfaga masiva de productores,
 * genera un Consumer Lag acumulativo observable en Kafka UI (http://localhost:8080).
 */
@Service
public class SlowConsumer {

    private static final Logger log = LoggerFactory.getLogger(SlowConsumer.class);

    private final AtomicLong processedCount = new AtomicLong(0);

    @KafkaListener(topics = "sensor-topic", groupId = "analytics-group")
    public void consume(String message) {
        try {
            // Simulación de procesamiento pesado (ej. transformaciones analíticas complejas o I/O lento)
            Thread.sleep(10);

            long total = processedCount.incrementAndGet();
            if (total % 500 == 0 || total == 1) {
                log.info("[SlowConsumer - analytics-group] Mensajes procesados: {}. Simulación de latencia (10ms/msg) activa.", total);
            } else {
                log.debug("[SlowConsumer] Mensaje procesado: {}", message);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[SlowConsumer] Hilo de consumo interrumpido", e);
        }
    }

    public long getProcessedCount() {
        return processedCount.get();
    }

    public void resetProcessedCount() {
        processedCount.set(0);
    }
}

