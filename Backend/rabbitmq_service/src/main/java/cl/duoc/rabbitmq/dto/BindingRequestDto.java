package cl.duoc.rabbitmq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class BindingRequestDto {

    @NotBlank(message = "El nombre de la cola no puede estar vacío")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "El nombre de la cola solo puede contener caracteres alfanuméricos, puntos, guiones y guiones bajos"
    )
    @Size(max = 255, message = "El nombre de la cola no puede exceder los 255 caracteres")
    private String queueName;

    @NotBlank(message = "El nombre del exchange no puede estar vacío")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "El nombre del exchange solo puede contener caracteres alfanuméricos, puntos, guiones y guiones bajos"
    )
    @Size(max = 255, message = "El nombre del exchange no puede exceder los 255 caracteres")
    private String exchangeName;

    @NotBlank(message = "La routing key no puede estar vacía")
    @Pattern(
            regexp = "^[a-zA-Z0-9._#*-]+$",
            message = "La routing key solo puede contener caracteres alfanuméricos, puntos, guiones, guiones bajos, '#' y '*'"
    )
    @Size(max = 255, message = "La routing key no puede exceder los 255 caracteres")
    private String routingKey;

    public BindingRequestDto() {
    }

    public BindingRequestDto(String queueName, String exchangeName, String routingKey) {
        this.queueName = queueName;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }
}

