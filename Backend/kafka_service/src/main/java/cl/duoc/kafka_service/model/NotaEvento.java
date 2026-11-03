package cl.duoc.kafka_service.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Modelo de Evento JSON para el Tópico 1 ("academic-grades-input").
 * Compatible con formato estándar: (id, estudianteId, asignatura, calificacion, fecha)
 * y con las propiedades previas (idNota, idEstudiante, nota, fechaCreacion).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotaEvento implements Serializable {

    @JsonAlias({"idNota"})
    private Integer id;

    @JsonAlias({"id"})
    private Integer idNota;

    @JsonAlias({"idIntegrante", "idEstudiante"})
    private Integer estudianteId;

    @JsonAlias({"estudianteId", "idIntegrante"})
    private Integer idEstudiante;

    @JsonAlias({"estudianteId", "idEstudiante"})
    private Integer idIntegrante;

    @JsonAlias({"calificacion"})
    private Double nota;

    @JsonAlias({"nota"})
    private Double calificacion;

    private String asignatura;
    private Integer idPonderacion;

    @JsonAlias({"fechaCreacion"})
    private String fecha;

    @JsonAlias({"fecha"})
    private String fechaCreacion;

    public NotaEvento() {}

    public NotaEvento(Integer idNota, Integer idEstudiante, Integer idIntegrante, Double nota,
                      Double calificacion, String asignatura, Integer idPonderacion, String fechaCreacion) {
        this.id = idNota;
        this.idNota = idNota;
        this.estudianteId = idEstudiante != null ? idEstudiante : idIntegrante;
        this.idEstudiante = idEstudiante;
        this.idIntegrante = idIntegrante;
        this.nota = nota != null ? nota : calificacion;
        this.calificacion = calificacion != null ? calificacion : nota;
        this.asignatura = asignatura;
        this.idPonderacion = idPonderacion;
        this.fecha = fechaCreacion;
        this.fechaCreacion = fechaCreacion;
    }

    public Double obtenerNotaEfectiva() {
        if (calificacion != null) {
            return calificacion;
        }
        return nota;
    }

    public Integer obtenerEstudianteId() {
        if (estudianteId != null) {
            return estudianteId;
        }
        if (idEstudiante != null) {
            return idEstudiante;
        }
        return idIntegrante;
    }

    public Integer getId() { return id != null ? id : idNota; }
    public void setId(Integer id) { this.id = id; this.idNota = id; }

    public Integer getIdNota() { return idNota != null ? idNota : id; }
    public void setIdNota(Integer idNota) { this.idNota = idNota; this.id = idNota; }

    public Integer getEstudianteId() { return obtenerEstudianteId(); }
    public void setEstudianteId(Integer estudianteId) { this.estudianteId = estudianteId; this.idEstudiante = estudianteId; }

    public Integer getIdEstudiante() { return obtenerEstudianteId(); }
    public void setIdEstudiante(Integer idEstudiante) { this.idEstudiante = idEstudiante; this.estudianteId = idEstudiante; }

    public Integer getIdIntegrante() { return idIntegrante; }
    public void setIdIntegrante(Integer idIntegrante) { this.idIntegrante = idIntegrante; }

    public Double getNota() { return obtenerNotaEfectiva(); }
    public void setNota(Double nota) { this.nota = nota; if (this.calificacion == null) this.calificacion = nota; }

    public Double getCalificacion() { return obtenerNotaEfectiva(); }
    public void setCalificacion(Double calificacion) { this.calificacion = calificacion; if (this.nota == null) this.nota = calificacion; }

    public String getAsignatura() { return asignatura; }
    public void setAsignatura(String asignatura) { this.asignatura = asignatura; }

    public Integer getIdPonderacion() { return idPonderacion; }
    public void setIdPonderacion(Integer idPonderacion) { this.idPonderacion = idPonderacion; }

    public String getFecha() { return fecha != null ? fecha : fechaCreacion; }
    public void setFecha(String fecha) { this.fecha = fecha; this.fechaCreacion = fecha; }

    public String getFechaCreacion() { return fechaCreacion != null ? fechaCreacion : fecha; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; this.fecha = fechaCreacion; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer idNota;
        private Integer estudianteId;
        private Integer idEstudiante;
        private Integer idIntegrante;
        private Double nota;
        private Double calificacion;
        private String asignatura;
        private Integer idPonderacion;
        private String fecha;
        private String fechaCreacion;

        public Builder id(Integer id) { this.id = id; this.idNota = id; return this; }
        public Builder idNota(Integer idNota) { this.idNota = idNota; this.id = idNota; return this; }
        public Builder estudianteId(Integer estudianteId) { this.estudianteId = estudianteId; this.idEstudiante = estudianteId; return this; }
        public Builder idEstudiante(Integer idEstudiante) { this.idEstudiante = idEstudiante; this.estudianteId = idEstudiante; return this; }
        public Builder idIntegrante(Integer idIntegrante) { this.idIntegrante = idIntegrante; return this; }
        public Builder nota(Double nota) { this.nota = nota; this.calificacion = nota; return this; }
        public Builder calificacion(Double calificacion) { this.calificacion = calificacion; this.nota = calificacion; return this; }
        public Builder asignatura(String asignatura) { this.asignatura = asignatura; return this; }
        public Builder idPonderacion(Integer idPonderacion) { this.idPonderacion = idPonderacion; return this; }
        public Builder fecha(String fecha) { this.fecha = fecha; this.fechaCreacion = fecha; return this; }
        public Builder fechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; this.fecha = fechaCreacion; return this; }

        public NotaEvento build() {
            NotaEvento evento = new NotaEvento(idNota != null ? idNota : id,
                    idEstudiante != null ? idEstudiante : estudianteId,
                    idIntegrante, nota != null ? nota : calificacion,
                    calificacion != null ? calificacion : nota,
                    asignatura, idPonderacion,
                    fechaCreacion != null ? fechaCreacion : fecha);
            evento.setId(id != null ? id : idNota);
            evento.setEstudianteId(estudianteId != null ? estudianteId : idEstudiante);
            evento.setFecha(fecha != null ? fecha : fechaCreacion);
            return evento;
        }
    }
}
