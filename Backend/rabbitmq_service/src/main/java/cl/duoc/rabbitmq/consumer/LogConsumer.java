package cl.duoc.rabbitmq.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LogConsumer {

    private static final Logger log = LoggerFactory.getLogger(LogConsumer.class);

    @Value("${rabbitmq.queues.all-logs:all_logs_queue}")
    private String allLogsQueue;

    @Value("${rabbitmq.queues.errors-only:errors_only_queue}")
    private String errorsOnlyQueue;

    @RabbitListener(queues = "${rabbitmq.queues.all-logs:all_logs_queue}")
    public void receiveAllLogs(String message) {
        log.info("[MONITOR GENERAL] Log recibido: {}", message);
    }

    @RabbitListener(queues = "${rabbitmq.queues.errors-only:errors_only_queue}")
    public void receiveErrorLogs(String message) {
        log.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        log.error("[ALERTA CRÍTICA] Error detectado: {}", message);
        log.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    public String getAllLogsQueue() {
        return allLogsQueue;
    }

    public String getErrorsOnlyQueue() {
        return errorsOnlyQueue;
    }
}
