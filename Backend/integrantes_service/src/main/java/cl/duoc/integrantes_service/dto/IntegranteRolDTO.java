package cl.duoc.integrantes_service.dto;

import lombok.Data;

@Data
public class IntegranteRolDTO {
    private Integer idIntegrante;
    private String nombre;
    private String apellido;
    private String correoElectronico;
    private RolDTO rol;
}