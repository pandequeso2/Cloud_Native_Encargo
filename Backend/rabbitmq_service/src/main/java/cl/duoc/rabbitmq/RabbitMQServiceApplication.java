package cl.duoc.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RabbitMQServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitMQServiceApplication.class, args);
    }
}
