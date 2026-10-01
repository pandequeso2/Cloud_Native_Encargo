package cl.duoc.rabbitmq.controller;

import cl.duoc.rabbitmq.config.RabbitMQConfig;
import cl.duoc.rabbitmq.service.RabbitResourceManager;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/rabbitmq/orders")
public class OrderController {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitResourceManager resourceManager;

    public OrderController(RabbitTemplate rabbitTemplate, RabbitResourceManager resourceManager) {
        this.rabbitTemplate = rabbitTemplate;
        this.resourceManager = resourceManager;
    }

    @PostMapping("/send")
    public Map<String, Object> sendOrder(@RequestBody Map<String, String> payload) {
        String orderId = payload.getOrDefault("orderId", "ORD-" + System.currentTimeMillis());
        String customerName = payload.getOrDefault("customerName", "Estudiante Duoc");
        String message = String.format("Orden ID: %s | Cliente: %s | Hora: %s",
                orderId,
                customerName,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDERS_EXCHANGE,
                RabbitMQConfig.ORDERS_ROUTING_KEY,
                message
        );

        Map<String, Object> response = new HashMap<>();
        response.put("status", "Orden enviada a RabbitMQ");
        response.put("orderId", orderId);
        response.put("message", message);
        return response;
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("backend", "Online");
        status.put("rabbitmq", "Conectado");
        status.put("timestamp", LocalDateTime.now().toString());

        QueueInformation mainQueue = resourceManager.getQueueInfo(RabbitMQConfig.ORDERS_QUEUE);
        QueueInformation dlqQueue = resourceManager.getQueueInfo(RabbitMQConfig.DLQ_QUEUE);

        if (mainQueue != null) {
            status.put("ordersQueueMessages", mainQueue.getMessageCount());
        }
        if (dlqQueue != null) {
            status.put("dlqMessages", dlqQueue.getMessageCount());
        }

        return status;
    }
}
