package cl.duoc.notas_service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Modelo de Evento JSON para el Tópico 1 ("academic-grades-input").
 * Cumple con el estándar del Plan de Trabajo: (id, estudianteId, asignatura, calificacion, fecha).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotaEvento implements Serializable {

    @JsonAlias({"idNota"})
    private Integer id;

    @JsonAlias({"idNota"})
    private Integer idNota;

    @JsonAlias({"idIntegrante", "idEstudiante"})
    private Integer estudianteId;

    @JsonAlias({"idIntegrante", "estudianteId"})
    private Integer idEstudiante;

    @JsonAlias({"idIntegrante", "idEstudiante"})
    private Integer idIntegrante;

    private String asignatura;

    @JsonAlias({"nota"})
    private Double calificacion;

    @JsonAlias({"calificacion"})
    private Double nota;

    @JsonAlias({"fechaCreacion"})
    private String fecha;

    @JsonAlias({"fecha"})
    private String fechaCreacion;

    private Integer idPonderacion;

    public Integer getId() {
        return id != null ? id : idNota;
    }

    public Integer getEstudianteId() {
        if (estudianteId != null) return estudianteId;
        if (idEstudiante != null) return idEstudiante;
        return idIntegrante;
    }

    public Double getCalificacion() {
        return calificacion != null ? calificacion : nota;
    }

    public String getFecha() {
        return fecha != null ? fecha : fechaCreacion;
    }
}
