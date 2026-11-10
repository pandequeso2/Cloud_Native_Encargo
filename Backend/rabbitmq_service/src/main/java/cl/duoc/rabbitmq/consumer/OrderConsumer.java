package cl.duoc.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderConsumer.class);

    @Value("${rabbitmq.queues.orders:orders.queue}")
    private String ordersQueue;

    @Value("${rabbitmq.queues.orders-dlq:orders.dlq}")
    private String dlqQueue;

    /**
     * Consumidor principal: Recibe órdenes de la cola principal.
     * Simula fallos aleatorios para demostrar el envío a Dead Letter Exchange (DLX).
     */
    @RabbitListener(
            id = "order-listener",
            queues = "${rabbitmq.queues.orders:orders.queue}",
            containerFactory = "orderListenerFactory"
    )
    public void processOrder(
            String message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) {
        try {
            log.info("[PROCESADOR] Recibida orden: {}", message);

            // SIMULACIÓN DE FALLO ALEATORIO (50% de probabilidad)
            if (Math.random() < 0.5) {
                throw new RuntimeException("Error simulado al procesar: " + message);
            }

            // Si el procesamiento es exitoso
            log.info("[✔ ÉXITO] Orden procesada correctamente: {}", message);
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.warn("[⚠ ERROR] {}. Enviando a Dead Letter Queue...", e.getMessage());
            try {
                // Nack con requeue=false: Va directamente al DLX (Dead Letter Exchange)
                channel.basicNack(deliveryTag, false, false);
                log.info("[→ DLX] Mensaje redirigido a Dead Letter Exchange");
            } catch (Exception nackException) {
                log.error("Error al enviar NACK manual: {}", nackException.getMessage());
            }
        }
    }

    /**
     * Consumidor de DLQ: Monitorea los mensajes que fallaron definitivamente.
     */
    @RabbitListener(queues = "${rabbitmq.queues.orders-dlq:orders.dlq}")
    public void processDLQ(String message) {
        log.error("☠ [DLQ] MENSAJE EN CUARENTENA: {}", message);
        log.error(" → Guardado en DLQ para auditoría y análisis.");
    }

    public String getOrdersQueue() {
        return ordersQueue;
    }

    public String getDlqQueue() {
        return dlqQueue;
    }
}
