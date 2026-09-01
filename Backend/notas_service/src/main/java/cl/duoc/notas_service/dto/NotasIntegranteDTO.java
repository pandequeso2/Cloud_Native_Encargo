package cl.duoc.notas_service.dto;

import cl.duoc.notas_service.model.Nota;
import lombok.Data;

import java.util.List;

@Data
public class NotasIntegranteDTO {

    private IntegranteDTO integrante;
    private List<Nota> notas;
}
