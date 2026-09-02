package cl.duoc.profesores_service.dto;

import lombok.Data;

@Data
public class SeccionDTO {
    private Integer idSeccion;
    private String codigoSeccion;
    private Integer capacidadMaxima;
    private Integer idProfesor;
}
