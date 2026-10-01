package cl.duoc.rabbitmq.controller;

import cl.duoc.rabbitmq.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/rabbitmq/logs")
public class LogProducerController {

    private final RabbitTemplate rabbitTemplate;

    public LogProducerController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    public String sendLog(@RequestBody LogRequest request) {
        String level = request.getLevel() != null ? request.getLevel().toUpperCase() : "INFO";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String formattedMessage = String.format("[%s] [%s] %s", timestamp, level, request.getMessage());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                level,
                formattedMessage
        );

        return "Log enviado con éxito | Nivel: " + level + " | Mensaje: " + formattedMessage;
    }

    public static class LogRequest {
        private String level; // INFO, WARNING, ERROR
        private String message;

        public LogRequest() {}

        public LogRequest(String level, String message) {
            this.level = level;
            this.message = message;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
