package cl.duoc.kafka_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamsConfig {

    @Value("${spring.kafka.streams.bootstrap-servers:${spring.kafka.bootstrap-servers:localhost:29092}}")
    private String bootstrapServers;

    @Value("${spring.kafka.streams.application-id:academic-pipeline-app}")
    private String applicationId;

    @Value("${app.kafka.topics.grades-input:academic-grades-input}")
    private String gradesInputTopic;

    @Value("${app.kafka.topics.alerts-output:academic-alerts-output}")
    private String alertsOutputTopic;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public NewTopic gradesInputTopic() {
        return TopicBuilder.name(gradesInputTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic alertsOutputTopic() {
        return TopicBuilder.name(alertsOutputTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}

