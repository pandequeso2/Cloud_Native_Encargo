package cl.duoc.rabbitmq.controller;

import cl.duoc.rabbitmq.dto.BindingRequestDto;
import cl.duoc.rabbitmq.dto.ExchangeRequestDto;
import cl.duoc.rabbitmq.dto.QueueRequestDto;
import cl.duoc.rabbitmq.service.RabbitResourceManager;
import jakarta.validation.Valid;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    public ResponseEntity<String> createQueue(@Valid @ModelAttribute QueueRequestDto dto) {
        resourceManager.createQueue(dto.getQueueName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Cola '" + dto.getQueueName() + "' creada exitosamente");
    }

    @PostMapping(value = "/queues", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createQueueJson(@Valid @RequestBody QueueRequestDto dto) {
        return createQueue(dto);
    }

    @PostMapping("/exchanges")
    public ResponseEntity<String> createExchange(@Valid @ModelAttribute ExchangeRequestDto dto) {
        resourceManager.createExchange(dto.getExchangeName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Exchange '" + dto.getExchangeName() + "' creado exitosamente");
    }

    @PostMapping(value = "/exchanges", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createExchangeJson(@Valid @RequestBody ExchangeRequestDto dto) {
        return createExchange(dto);
    }

    @PostMapping("/bindings")
    public ResponseEntity<String> createBinding(@Valid @ModelAttribute BindingRequestDto dto) {
        resourceManager.createBinding(dto.getQueueName(), dto.getExchangeName(), dto.getRoutingKey());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Binding creado: " + dto.getQueueName() + " <-> " + dto.getExchangeName() + " (RoutingKey: " + dto.getRoutingKey() + ")");
    }

    @PostMapping(value = "/bindings", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createBindingJson(@Valid @RequestBody BindingRequestDto dto) {
        return createBinding(dto);
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
