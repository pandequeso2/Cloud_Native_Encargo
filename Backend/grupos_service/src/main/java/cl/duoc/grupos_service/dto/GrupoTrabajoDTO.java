package cl.duoc.grupos_service.dto;

import lombok.Data;

@Data
public class GrupoTrabajoDTO {
    private Integer idGrupo;
    private String nombreGrupo;
    private Boolean grupoLleno;
    private TrabajoDTO trabajo;
}