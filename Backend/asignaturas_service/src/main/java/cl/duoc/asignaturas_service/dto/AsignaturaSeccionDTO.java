package cl.duoc.asignaturas_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class AsignaturaSeccionDTO {

    private Integer idAsignatura;
    private String nombreAsignatura;
    private Integer idProfesor;
    private List<SeccionDTO> secciones;
}