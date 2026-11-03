package cl.duoc.kafka_service.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Modelo de Evento JSON para el Tópico 1 Salida ("academic-alerts-output").
 * Cumple con el estándar del Plan de Trabajo: (estudianteId, asignatura, calificacion, mensajeAlerta, fechaAlerta).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlertaAcademica implements Serializable {

    private String idAlerta;

    @JsonAlias({"idEstudiante"})
    private Integer estudianteId;

    @JsonAlias({"estudianteId"})
    private Integer idEstudiante;

    private Integer idNota;
    private String asignatura;
    private Double calificacion;
    private String nivelAlerta;

    @JsonAlias({"mensaje"})
    private String mensajeAlerta;

    @JsonAlias({"mensajeAlerta"})
    private String mensaje;

    private String fechaAlerta;

    public AlertaAcademica() {}

    public AlertaAcademica(String idAlerta, Integer idEstudiante, Integer idNota, String asignatura,
                           Double calificacion, String nivelAlerta, String mensaje, String fechaAlerta) {
        this.idAlerta = idAlerta;
        this.estudianteId = idEstudiante;
        this.idEstudiante = idEstudiante;
        this.idNota = idNota;
        this.asignatura = asignatura;
        this.calificacion = calificacion;
        this.nivelAlerta = nivelAlerta;
        this.mensaje = mensaje;
        this.mensajeAlerta = mensaje;
        this.fechaAlerta = fechaAlerta;
    }

    public String getIdAlerta() { return idAlerta; }
    public void setIdAlerta(String idAlerta) { this.idAlerta = idAlerta; }

    public Integer getEstudianteId() { return estudianteId != null ? estudianteId : idEstudiante; }
    public void setEstudianteId(Integer estudianteId) { this.estudianteId = estudianteId; this.idEstudiante = estudianteId; }

    public Integer getIdEstudiante() { return idEstudiante != null ? idEstudiante : estudianteId; }
    public void setIdEstudiante(Integer idEstudiante) { this.idEstudiante = idEstudiante; this.estudianteId = idEstudiante; }

    public Integer getIdNota() { return idNota; }
    public void setIdNota(Integer idNota) { this.idNota = idNota; }

    public String getAsignatura() { return asignatura; }
    public void setAsignatura(String asignatura) { this.asignatura = asignatura; }

    public Double getCalificacion() { return calificacion; }
    public void setCalificacion(Double calificacion) { this.calificacion = calificacion; }

    public String getNivelAlerta() { return nivelAlerta; }
    public void setNivelAlerta(String nivelAlerta) { this.nivelAlerta = nivelAlerta; }

    public String getMensajeAlerta() { return mensajeAlerta != null ? mensajeAlerta : mensaje; }
    public void setMensajeAlerta(String mensajeAlerta) { this.mensajeAlerta = mensajeAlerta; this.mensaje = mensajeAlerta; }

    public String getMensaje() { return mensaje != null ? mensaje : mensajeAlerta; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; this.mensajeAlerta = mensaje; }

    public String getFechaAlerta() { return fechaAlerta; }
    public void setFechaAlerta(String fechaAlerta) { this.fechaAlerta = fechaAlerta; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String idAlerta;
        private Integer estudianteId;
        private Integer idEstudiante;
        private Integer idNota;
        private String asignatura;
        private Double calificacion;
        private String nivelAlerta;
        private String mensajeAlerta;
        private String mensaje;
        private String fechaAlerta;

        public Builder idAlerta(String idAlerta) { this.idAlerta = idAlerta; return this; }
        public Builder estudianteId(Integer estudianteId) { this.estudianteId = estudianteId; this.idEstudiante = estudianteId; return this; }
        public Builder idEstudiante(Integer idEstudiante) { this.idEstudiante = idEstudiante; this.estudianteId = idEstudiante; return this; }
        public Builder idNota(Integer idNota) { this.idNota = idNota; return this; }
        public Builder asignatura(String asignatura) { this.asignatura = asignatura; return this; }
        public Builder calificacion(Double calificacion) { this.calificacion = calificacion; return this; }
        public Builder nivelAlerta(String nivelAlerta) { this.nivelAlerta = nivelAlerta; return this; }
        public Builder mensajeAlerta(String mensajeAlerta) { this.mensajeAlerta = mensajeAlerta; this.mensaje = mensajeAlerta; return this; }
        public Builder mensaje(String mensaje) { this.mensaje = mensaje; this.mensajeAlerta = mensaje; return this; }
        public Builder fechaAlerta(String fechaAlerta) { this.fechaAlerta = fechaAlerta; return this; }

        public AlertaAcademica build() {
            AlertaAcademica alerta = new AlertaAcademica(idAlerta,
                    idEstudiante != null ? idEstudiante : estudianteId,
                    idNota, asignatura, calificacion, nivelAlerta,
                    mensaje != null ? mensaje : mensajeAlerta,
                    fechaAlerta);
            alerta.setEstudianteId(estudianteId != null ? estudianteId : idEstudiante);
            alerta.setMensajeAlerta(mensajeAlerta != null ? mensajeAlerta : mensaje);
            return alerta;
        }
    }
}
