package cl.duoc.rabbitmq.controller;

import cl.duoc.rabbitmq.service.RabbitResourceManager;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${rabbitmq.exchanges.orders:orders.exchange}")
    private String ordersExchange;

    @Value("${rabbitmq.routing-keys.order-created:order.created}")
    private String ordersRoutingKey;

    @Value("${rabbitmq.queues.orders:orders.queue}")
    private String ordersQueue;

    @Value("${rabbitmq.queues.orders-dlq:orders.dlq}")
    private String dlqQueue;

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
                ordersExchange,
                ordersRoutingKey,
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

        QueueInformation mainQueue = resourceManager.getQueueInfo(ordersQueue);
        QueueInformation dlq = resourceManager.getQueueInfo(dlqQueue);

        if (mainQueue != null) {
            status.put("ordersQueueMessages", mainQueue.getMessageCount());
        }
        if (dlq != null) {
            status.put("dlqMessages", dlq.getMessageCount());
        }

        return status;
    }

    public String getOrdersExchange() {
        return ordersExchange;
    }

    public String getOrdersRoutingKey() {
        return ordersRoutingKey;
    }

    public String getOrdersQueue() {
        return ordersQueue;
    }

    public String getDlqQueue() {
        return dlqQueue;
    }
}
