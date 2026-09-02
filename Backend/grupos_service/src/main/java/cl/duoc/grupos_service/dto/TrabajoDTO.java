package cl.duoc.grupos_service.dto;

import lombok.Data;

@Data
public class TrabajoDTO {
    private Integer idTrabajo;
    private String nombreTrabajo;
    private Double porcentajeNota;
    private String tipoTrabajo;
    private Integer semestre;
    private String estado;
}