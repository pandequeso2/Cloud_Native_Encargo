package cl.duoc.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // =========================================================================
    // 1. SISTEMA DE LOGS (Direct Exchange, Bindings, Routing Keys)
    // =========================================================================
    public static final String EXCHANGE_NAME = "logs_direct_exchange";
    public static final String ALL_LOGS_QUEUE = "all_logs_queue";
    public static final String ERRORS_ONLY_QUEUE = "errors_only_queue";

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue allLogsQueue() {
        return new Queue(ALL_LOGS_QUEUE, true);
    }

    @Bean
    public Queue errorsOnlyQueue() {
        return new Queue(ERRORS_ONLY_QUEUE, true);
    }

    @Bean
    public Binding bindAllLogsForInfo(DirectExchange directExchange, Queue allLogsQueue) {
        return BindingBuilder.bind(allLogsQueue).to(directExchange).with("INFO");
    }

    @Bean
    public Binding bindAllLogsForWarning(DirectExchange directExchange, Queue allLogsQueue) {
        return BindingBuilder.bind(allLogsQueue).to(directExchange).with("WARNING");
    }

    @Bean
    public Binding bindAllLogsForError(DirectExchange directExchange, Queue allLogsQueue) {
        return BindingBuilder.bind(allLogsQueue).to(directExchange).with("ERROR");
    }

    @Bean
    public Binding bindErrorsOnly(DirectExchange directExchange, Queue errorsOnlyQueue) {
        return BindingBuilder.bind(errorsOnlyQueue).to(directExchange).with("ERROR");
    }

    // =========================================================================
    // 2. SISTEMA RESILIENTE CON DLX / DLQ Y TTL (Dead Letter Queue & Retries)
    // =========================================================================
    public static final String ORDERS_EXCHANGE = "orders.exchange";
    public static final String ORDERS_QUEUE = "orders.queue";
    public static final String ORDERS_ROUTING_KEY = "order.created";

    public static final String DLX_EXCHANGE = "orders.dlx";
    public static final String DLQ_QUEUE = "orders.dlq";
    public static final String DLX_ROUTING_KEY = "order.dead";

    @Bean
    public DirectExchange ordersExchange() {
        return new DirectExchange(ORDERS_EXCHANGE, true, false);
    }

    @Bean
    public Queue ordersQueue() {
        return QueueBuilder.durable(ORDERS_QUEUE)
                .withArgument("x-message-ttl", 30000) // TTL de 30 segundos
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY)
                .withArgument("x-max-length", 1000)
                .build();
    }

    @Bean
    public Binding ordersBinding(Queue ordersQueue, DirectExchange ordersExchange) {
        return BindingBuilder.bind(ordersQueue)
                .to(ordersExchange)
                .with(ORDERS_ROUTING_KEY);
    }

    @Bean
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange(DLX_EXCHANGE, true, false);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_QUEUE)
                .withArgument("x-message-ttl", 86400000) // TTL de 24 horas en DLQ
                .build();
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, FanoutExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange);
    }
}
