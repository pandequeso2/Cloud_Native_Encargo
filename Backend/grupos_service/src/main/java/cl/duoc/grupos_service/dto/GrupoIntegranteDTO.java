package cl.duoc.grupos_service.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({
        "idGrupo",
        "nombreGrupo",
        "grupoLleno"
})
public class GrupoIntegranteDTO {

    private Integer idGrupo;
    private String nombreGrupo;
    private Boolean grupoLleno;

    private List<IntegranteDTO> integrantes;
}
