package cl.duoc.kafka_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SlowConsumerTest {

    private SlowConsumer slowConsumer;

    @BeforeEach
    void setUp() {
        slowConsumer = new SlowConsumer();
    }

    @Test
    void testConsumeIncrementsCount() {
        assertEquals(0, slowConsumer.getProcessedCount());
        slowConsumer.consume("{\"id\":1,\"valor\":100}");
        assertEquals(1, slowConsumer.getProcessedCount());

        slowConsumer.resetProcessedCount();
        assertEquals(0, slowConsumer.getProcessedCount());
    }
}

