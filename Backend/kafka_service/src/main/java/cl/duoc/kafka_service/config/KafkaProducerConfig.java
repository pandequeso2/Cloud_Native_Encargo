package cl.duoc.kafka_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración de Productor de Alto Rendimiento (High-Throughput Producer Tuning)
 * Optimizado para procesamiento por lotes masivo, compresión Snappy y reducción de latencia de red.
 */
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:29092}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Tuning de Rendimiento:
        // 1. Tamaño de lote aumentado a 32 KB para maximizar empaquetamiento
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768);

        // 2. Tiempo de espera de 20ms antes de despachar el lote (mejora el throughput bajo carga)
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 20);

        // 3. Compresión Snappy: balance óptimo entre ratio de compresión y uso eficiente de CPU
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic sensorTopic() {
        return TopicBuilder.name("sensor-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}

