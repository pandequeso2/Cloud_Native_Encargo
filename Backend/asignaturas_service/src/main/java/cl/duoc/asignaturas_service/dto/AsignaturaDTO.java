package cl.duoc.asignaturas_service.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class AsignaturaDTO {

    private Integer idAsignatura;
    private String nombreAsignatura;
    private Integer idProfesor;

}
