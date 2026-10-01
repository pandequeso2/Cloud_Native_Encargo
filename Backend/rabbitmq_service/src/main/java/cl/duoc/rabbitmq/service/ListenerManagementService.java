package cl.duoc.rabbitmq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ListenerManagementService {

    private static final Logger log = LoggerFactory.getLogger(ListenerManagementService.class);
    private final RabbitListenerEndpointRegistry registry;

    public ListenerManagementService(RabbitListenerEndpointRegistry registry) {
        this.registry = registry;
    }

    public void pauseListener(String listenerId) {
        try {
            MessageListenerContainer container = registry.getListenerContainer(listenerId);
            if (container != null && container.isRunning()) {
                container.stop();
                log.info("⏸ Listener pausado / detenido: {}", listenerId);
            }
        } catch (Exception e) {
            log.error("Error al pausar listener {}: {}", listenerId, e.getMessage());
        }
    }

    public void resumeListener(String listenerId) {
        try {
            MessageListenerContainer container = registry.getListenerContainer(listenerId);
            if (container != null && !container.isRunning()) {
                container.start();
                log.info("▶ Listener reanudado / iniciado: {}", listenerId);
            }
        } catch (Exception e) {
            log.error("Error al reanudar listener {}: {}", listenerId, e.getMessage());
        }
    }

    public List<String> getAllListenerIds() {
        return new ArrayList<>(registry.getListenerContainerIds());
    }
}
