package cl.duoc.asignaturas_service.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class UnidadDTO {


    private int idUnidad;


    private int idAsignatura;


    private int numeroUnidad;

    private String nombreUnidad;
}
