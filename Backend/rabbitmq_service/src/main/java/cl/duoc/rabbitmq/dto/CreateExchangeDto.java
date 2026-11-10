package cl.duoc.rabbitmq.dto;

public class CreateExchangeDto extends ExchangeRequestDto {
    public CreateExchangeDto() {
        super();
    }

    public CreateExchangeDto(String exchangeName) {
        super(exchangeName);
    }
}

