package cl.duoc.profesores_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProfesorSeccionDTO {

    private Integer idProfesor;
    private String nombre;
    private String apellido;
    private String correoElectronico;

    private List<SeccionDTO> secciones;
}
