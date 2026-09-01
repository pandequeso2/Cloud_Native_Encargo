package cl.duoc.secciones_service.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "idSeccion",
        "codigoSeccion",
        "capacidadMaxima"
})
public class SeccionIntegranteDTO {

    private Integer idSeccion;
    private String codigoSeccion;

    private IntegranteDTO integrante;
}
