package cl.duoc.notas_service.dto;

import lombok.Data;

@Data
public class IntegranteDTO {

    private Integer idIntegrante;
    private String nombre;
    private String apellido;
    private String correoElectronico;
    private Integer idRol;

}
