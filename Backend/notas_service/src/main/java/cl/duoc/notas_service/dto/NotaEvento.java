package cl.duoc.notas_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotaEvento implements Serializable {
    private Integer idNota;
    private Integer idEstudiante;
    private Integer idIntegrante;
    private Double nota;
    private Double calificacion;
    private String asignatura;
    private Integer idPonderacion;
    private String fechaCreacion;
}

