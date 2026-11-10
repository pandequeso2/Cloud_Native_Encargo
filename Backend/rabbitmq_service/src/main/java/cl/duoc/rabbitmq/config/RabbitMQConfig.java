package cl.duoc.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // =========================================================================
    // 1. SISTEMA DE LOGS (Direct Exchange, Bindings, Routing Keys)
    // =========================================================================
    @Value("${rabbitmq.exchanges.logs:logs_direct_exchange}")
    private String logsExchange;

    @Value("${rabbitmq.queues.all-logs:all_logs_queue}")
    private String allLogsQueue;

    @Value("${rabbitmq.queues.errors-only:errors_only_queue}")
    private String errorsOnlyQueue;

    @Value("${rabbitmq.routing-keys.info:INFO}")
    private String routingKeyInfo;

    @Value("${rabbitmq.routing-keys.warning:WARNING}")
    private String routingKeyWarning;

    @Value("${rabbitmq.routing-keys.error:ERROR}")
    private String routingKeyError;

    // =========================================================================
    // 2. SISTEMA RESILIENTE CON DLX / DLQ Y TTL (Dead Letter Queue & Retries)
    // =========================================================================
    @Value("${rabbitmq.exchanges.orders:orders.exchange}")
    private String ordersExchange;

    @Value("${rabbitmq.queues.orders:orders.queue}")
    private String ordersQueue;

    @Value("${rabbitmq.routing-keys.order-created:order.created}")
    private String ordersRoutingKey;

    @Value("${rabbitmq.exchanges.orders-dlx:orders.dlx}")
    private String dlxExchange;

    @Value("${rabbitmq.queues.orders-dlq:orders.dlq}")
    private String dlqQueue;

    @Value("${rabbitmq.routing-keys.order-dead:order.dead}")
    private String dlxRoutingKey;

    // --- Beans: Sistema de Logs ---

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(logsExchange, true, false);
    }

    @Bean
    public Queue allLogsQueue() {
        return new Queue(allLogsQueue, true);
    }

    @Bean
    public Queue errorsOnlyQueue() {
        return new Queue(errorsOnlyQueue, true);
    }

    @Bean
    public Binding bindAllLogsForInfo(DirectExchange directExchange, Queue allLogsQueue) {
        return BindingBuilder.bind(allLogsQueue).to(directExchange).with(routingKeyInfo);
    }

    @Bean
    public Binding bindAllLogsForWarning(DirectExchange directExchange, Queue allLogsQueue) {
        return BindingBuilder.bind(allLogsQueue).to(directExchange).with(routingKeyWarning);
    }

    @Bean
    public Binding bindAllLogsForError(DirectExchange directExchange, Queue allLogsQueue) {
        return BindingBuilder.bind(allLogsQueue).to(directExchange).with(routingKeyError);
    }

    @Bean
    public Binding bindErrorsOnly(DirectExchange directExchange, Queue errorsOnlyQueue) {
        return BindingBuilder.bind(errorsOnlyQueue).to(directExchange).with(routingKeyError);
    }

    // --- Beans: Sistema Resiliente de Órdenes y DLX / DLQ ---

    @Bean
    public DirectExchange ordersExchange() {
        return new DirectExchange(ordersExchange, true, false);
    }

    @Bean
    public Queue ordersQueue() {
        return QueueBuilder.durable(ordersQueue)
                .withArgument("x-message-ttl", 30000) // TTL de 30 segundos
                .withArgument("x-dead-letter-exchange", dlxExchange)
                .withArgument("x-dead-letter-routing-key", dlxRoutingKey)
                .withArgument("x-max-length", 1000)
                .build();
    }

    @Bean
    public Binding ordersBinding(Queue ordersQueue, DirectExchange ordersExchange) {
        return BindingBuilder.bind(ordersQueue)
                .to(ordersExchange)
                .with(ordersRoutingKey);
    }

    @Bean
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange(dlxExchange, true, false);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(dlqQueue)
                .withArgument("x-message-ttl", 86400000) // TTL de 24 horas en DLQ
                .build();
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, FanoutExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange);
    }

    // --- Getters ---

    public String getLogsExchange() {
        return logsExchange;
    }

    public String getAllLogsQueue() {
        return allLogsQueue;
    }

    public String getErrorsOnlyQueue() {
        return errorsOnlyQueue;
    }

    public String getRoutingKeyInfo() {
        return routingKeyInfo;
    }

    public String getRoutingKeyWarning() {
        return routingKeyWarning;
    }

    public String getRoutingKeyError() {
        return routingKeyError;
    }

    public String getOrdersExchange() {
        return ordersExchange;
    }

    public String getOrdersQueue() {
        return ordersQueue;
    }

    public String getOrdersRoutingKey() {
        return ordersRoutingKey;
    }

    public String getDlxExchange() {
        return dlxExchange;
    }

    public String getDlqQueue() {
        return dlqQueue;
    }

    public String getDlxRoutingKey() {
        return dlxRoutingKey;
    }
}
