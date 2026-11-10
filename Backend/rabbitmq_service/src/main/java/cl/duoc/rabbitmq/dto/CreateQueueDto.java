package cl.duoc.rabbitmq.dto;

public class CreateQueueDto extends QueueRequestDto {
    public CreateQueueDto() {
        super();
    }

    public CreateQueueDto(String queueName) {
        super(queueName);
    }
}

