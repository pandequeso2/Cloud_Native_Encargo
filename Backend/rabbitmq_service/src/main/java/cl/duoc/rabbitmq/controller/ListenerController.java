package cl.duoc.rabbitmq.controller;

import cl.duoc.rabbitmq.service.ListenerManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rabbitmq/listeners")
public class ListenerController {

    private final ListenerManagementService listenerService;

    public ListenerController(ListenerManagementService listenerService) {
        this.listenerService = listenerService;
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllListeners() {
        return ResponseEntity.ok(listenerService.getAllListenerIds());
    }

    @PostMapping("/{listenerId}/pause")
    public ResponseEntity<String> pauseListener(@PathVariable String listenerId) {
        listenerService.pauseListener(listenerId);
        return ResponseEntity.ok("Listener '" + listenerId + "' pausado correctamente");
    }

    @PostMapping("/{listenerId}/resume")
    public ResponseEntity<String> resumeListener(@PathVariable String listenerId) {
        listenerService.resumeListener(listenerId);
        return ResponseEntity.ok("Listener '" + listenerId + "' reanudado correctamente");
    }
}
