package cl.duoc.kafka_service.topology;

import cl.duoc.kafka_service.model.AlertaAcademica;
import cl.duoc.kafka_service.model.NotaEvento;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class PipelineTopologyTest {

    private TopologyTestDriver testDriver;
    private TestInputTopic<String, String> inputTopic;
    private TestOutputTopic<String, String> outputTopic;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        StreamsBuilder builder = new StreamsBuilder();
        PipelineTopology topology = new PipelineTopology(objectMapper);
        topology.buildPipeline(builder);

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-academic-pipeline");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");

        testDriver = new TopologyTestDriver(builder.build(), props);
        inputTopic = testDriver.createInputTopic(
                "academic-grades-input",
                new StringSerializer(),
                new StringSerializer()
        );
        outputTopic = testDriver.createOutputTopic(
                "academic-alerts-output",
                new StringDeserializer(),
                new StringDeserializer()
        );
    }

    @AfterEach
    void tearDown() {
        if (testDriver != null) {
            testDriver.close();
        }
    }

    @Test
    void testNotaDeficienteGeneraAlerta() throws Exception {
        NotaEvento notaDeficiente = NotaEvento.builder()
                .idNota(10)
                .idEstudiante(101)
                .nota(3.2)
                .asignatura("Arquitectura Cloud")
                .build();

        String json = objectMapper.writeValueAsString(notaDeficiente);
        inputTopic.pipeInput("101", json);

        assertFalse(outputTopic.isEmpty());
        KeyValue<String, String> record = outputTopic.readKeyValue();
        assertEquals("101", record.key);

        AlertaAcademica alerta = objectMapper.readValue(record.value, AlertaAcademica.class);
        assertEquals(101, alerta.getIdEstudiante());
        assertEquals(3.2, alerta.getCalificacion());
        assertEquals("CRITICA", alerta.getNivelAlerta());
        assertTrue(alerta.getMensaje().contains("calificación reprobatoria"));
    }

    @Test
    void testNotaAprobatoriaNoGeneraAlerta() throws Exception {
        NotaEvento notaAprobada = NotaEvento.builder()
                .idNota(11)
                .idEstudiante(102)
                .nota(5.5)
                .asignatura("Bases de Datos")
                .build();

        String json = objectMapper.writeValueAsString(notaAprobada);
        inputTopic.pipeInput("102", json);

        assertTrue(outputTopic.isEmpty());
    }

    @Test
    void testNotaCuatroPuntoCeroNoGeneraAlerta() throws Exception {
        NotaEvento notaCuatro = NotaEvento.builder()
                .idNota(12)
                .idEstudiante(103)
                .nota(4.0)
                .asignatura("Programación")
                .build();

        String json = objectMapper.writeValueAsString(notaCuatro);
        inputTopic.pipeInput("103", json);

        assertTrue(outputTopic.isEmpty());
    }
}

