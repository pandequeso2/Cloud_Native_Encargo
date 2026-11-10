package cl.duoc.rabbitmq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ExchangeRequestDto {

    @NotBlank(message = "El nombre del exchange no puede estar vacío")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "El nombre del exchange solo puede contener caracteres alfanuméricos, puntos, guiones y guiones bajos"
    )
    @Size(max = 255, message = "El nombre del exchange no puede exceder los 255 caracteres")
    private String exchangeName;

    public ExchangeRequestDto() {
    }

    public ExchangeRequestDto(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }
}

