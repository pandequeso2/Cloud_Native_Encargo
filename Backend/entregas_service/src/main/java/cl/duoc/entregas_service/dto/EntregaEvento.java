package cl.duoc.entregas_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntregaEvento implements Serializable {
    private Integer idEntrega;
    private String estudiante;
    private Integer idGrupo;
    private Integer idTrabajo;
    private String estado;
    private String fecha;
    private String accion;
}

