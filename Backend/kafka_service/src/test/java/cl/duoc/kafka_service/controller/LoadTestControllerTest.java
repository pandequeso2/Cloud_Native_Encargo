package cl.duoc.kafka_service.controller;

import cl.duoc.kafka_service.service.SlowConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoadTestControllerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private SlowConsumer slowConsumer;

    private LoadTestController controller;

    @BeforeEach
    void setUp() {
        controller = new LoadTestController(kafkaTemplate, slowConsumer);
    }

    @Test
    void testStartLoadTest() {
        int count = 100;
        ResponseEntity<Map<String, Object>> response = controller.startLoadTest(count);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("SUCCESS", body.get("status"));
        assertEquals("sensor-topic", body.get("topic"));
        assertEquals(count, body.get("messagesSent"));
        assertTrue((long) body.get("executionTimeMs") >= 0);
        assertTrue((double) body.get("throughputMsgsSec") >= 0.0);

        verify(kafkaTemplate, times(count)).send(eq("sensor-topic"), anyString(), anyString());
        verify(kafkaTemplate, times(1)).flush();
    }

    @Test
    void testGetStatus() {
        when(slowConsumer.getProcessedCount()).thenReturn(450L);

        ResponseEntity<Map<String, Object>> response = controller.getStatus();
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(450L, response.getBody().get("messagesProcessedBySlowConsumer"));
    }

    @Test
    void testReset() {
        ResponseEntity<Map<String, String>> response = controller.resetConsumerCounter();
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(slowConsumer, times(1)).resetProcessedCount();
    }
}

