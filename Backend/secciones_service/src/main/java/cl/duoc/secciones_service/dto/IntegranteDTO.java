package cl.duoc.secciones_service.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "idIntegrante",
        "nombre",
        "apellido"
})
public class IntegranteDTO {

    private Integer idIntegrante;
    private String nombre;
    private String apellido;
}
