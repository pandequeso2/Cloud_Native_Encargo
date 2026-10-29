package cl.duoc.kafka_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotaEvento implements Serializable {
    private Integer idNota;
    private Integer idEstudiante;
    private Integer idIntegrante;
    private Double nota;
    private Double calificacion;
    private String asignatura;
    private Integer idPonderacion;
    private String fechaCreacion;

    public NotaEvento() {}

    public NotaEvento(Integer idNota, Integer idEstudiante, Integer idIntegrante, Double nota,
                      Double calificacion, String asignatura, Integer idPonderacion, String fechaCreacion) {
        this.idNota = idNota;
        this.idEstudiante = idEstudiante;
        this.idIntegrante = idIntegrante;
        this.nota = nota;
        this.calificacion = calificacion;
        this.asignatura = asignatura;
        this.idPonderacion = idPonderacion;
        this.fechaCreacion = fechaCreacion;
    }

    public Double obtenerNotaEfectiva() {
        if (nota != null) {
            return nota;
        }
        return calificacion;
    }

    public Integer obtenerEstudianteId() {
        if (idEstudiante != null) {
            return idEstudiante;
        }
        return idIntegrante;
    }

    public Integer getIdNota() { return idNota; }
    public void setIdNota(Integer idNota) { this.idNota = idNota; }

    public Integer getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(Integer idEstudiante) { this.idEstudiante = idEstudiante; }

    public Integer getIdIntegrante() { return idIntegrante; }
    public void setIdIntegrante(Integer idIntegrante) { this.idIntegrante = idIntegrante; }

    public Double getNota() { return nota; }
    public void setNota(Double nota) { this.nota = nota; }

    public Double getCalificacion() { return calificacion; }
    public void setCalificacion(Double calificacion) { this.calificacion = calificacion; }

    public String getAsignatura() { return asignatura; }
    public void setAsignatura(String asignatura) { this.asignatura = asignatura; }

    public Integer getIdPonderacion() { return idPonderacion; }
    public void setIdPonderacion(Integer idPonderacion) { this.idPonderacion = idPonderacion; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer idNota;
        private Integer idEstudiante;
        private Integer idIntegrante;
        private Double nota;
        private Double calificacion;
        private String asignatura;
        private Integer idPonderacion;
        private String fechaCreacion;

        public Builder idNota(Integer idNota) { this.idNota = idNota; return this; }
        public Builder idEstudiante(Integer idEstudiante) { this.idEstudiante = idEstudiante; return this; }
        public Builder idIntegrante(Integer idIntegrante) { this.idIntegrante = idIntegrante; return this; }
        public Builder nota(Double nota) { this.nota = nota; return this; }
        public Builder calificacion(Double calificacion) { this.calificacion = calificacion; return this; }
        public Builder asignatura(String asignatura) { this.asignatura = asignatura; return this; }
        public Builder idPonderacion(Integer idPonderacion) { this.idPonderacion = idPonderacion; return this; }
        public Builder fechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; return this; }

        public NotaEvento build() {
            return new NotaEvento(idNota, idEstudiante, idIntegrante, nota, calificacion, asignatura, idPonderacion, fechaCreacion);
        }
    }
}
