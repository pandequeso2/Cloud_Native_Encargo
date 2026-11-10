package cl.duoc.rabbitmq.dto;

public class CreateBindingDto extends BindingRequestDto {
    public CreateBindingDto() {
        super();
    }

    public CreateBindingDto(String queueName, String exchangeName, String routingKey) {
        super(queueName, exchangeName, routingKey);
    }
}

