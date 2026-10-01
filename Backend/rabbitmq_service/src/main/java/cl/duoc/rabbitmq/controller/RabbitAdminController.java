package cl.duoc.rabbitmq.controller;

import cl.duoc.rabbitmq.service.RabbitResourceManager;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rabbitmq/admin")
public class RabbitAdminController {

    private final RabbitResourceManager resourceManager;

    public RabbitAdminController(RabbitResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @PostMapping("/queues")
    public ResponseEntity<String> createQueue(@RequestParam String queueName) {
        resourceManager.createQueue(queueName);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Cola '" + queueName + "' creada exitosamente");
    }

    @PostMapping("/exchanges")
    public ResponseEntity<String> createExchange(@RequestParam String exchangeName) {
        resourceManager.createExchange(exchangeName);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Exchange '" + exchangeName + "' creado exitosamente");
    }

    @PostMapping("/bindings")
    public ResponseEntity<String> createBinding(
            @RequestParam String queueName,
            @RequestParam String exchangeName,
            @RequestParam String routingKey) {
        resourceManager.createBinding(queueName, exchangeName, routingKey);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Binding creado: " + queueName + " <-> " + exchangeName + " (RoutingKey: " + routingKey + ")");
    }

    @GetMapping("/queues/{queueName}")
    public ResponseEntity<?> getQueueInfo(@PathVariable String queueName) {
        QueueInformation info = resourceManager.getQueueInfo(queueName);
        return info != null ? ResponseEntity.ok(info) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/queues/{queueName}")
    public ResponseEntity<String> deleteQueue(@PathVariable String queueName) {
        resourceManager.deleteQueue(queueName);
        return ResponseEntity.ok("Cola '" + queueName + "' eliminada");
    }

    @PostMapping("/queues/{queueName}/purge")
    public ResponseEntity<String> purgeQueue(@PathVariable String queueName) {
        resourceManager.purgeQueue(queueName);
        return ResponseEntity.ok("Cola '" + queueName + "' purgada");
    }
}
