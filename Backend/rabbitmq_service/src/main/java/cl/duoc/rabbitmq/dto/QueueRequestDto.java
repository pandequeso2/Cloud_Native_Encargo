package cl.duoc.rabbitmq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class QueueRequestDto {

    @NotBlank(message = "El nombre de la cola no puede estar vacío")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "El nombre de la cola solo puede contener caracteres alfanuméricos, puntos, guiones y guiones bajos"
    )
    @Size(max = 255, message = "El nombre de la cola no puede exceder los 255 caracteres")
    private String queueName;

    public QueueRequestDto() {
    }

    public QueueRequestDto(String queueName) {
        this.queueName = queueName;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
}

