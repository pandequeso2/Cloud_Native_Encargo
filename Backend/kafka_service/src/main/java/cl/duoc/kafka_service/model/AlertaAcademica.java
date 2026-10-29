package cl.duoc.kafka_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlertaAcademica implements Serializable {
    private String idAlerta;
    private Integer idEstudiante;
    private Integer idNota;
    private String asignatura;
    private Double calificacion;
    private String nivelAlerta;
    private String mensaje;
    private String fechaAlerta;

    public AlertaAcademica() {}

    public AlertaAcademica(String idAlerta, Integer idEstudiante, Integer idNota, String asignatura,
                           Double calificacion, String nivelAlerta, String mensaje, String fechaAlerta) {
        this.idAlerta = idAlerta;
        this.idEstudiante = idEstudiante;
        this.idNota = idNota;
        this.asignatura = asignatura;
        this.calificacion = calificacion;
        this.nivelAlerta = nivelAlerta;
        this.mensaje = mensaje;
        this.fechaAlerta = fechaAlerta;
    }

    public String getIdAlerta() { return idAlerta; }
    public void setIdAlerta(String idAlerta) { this.idAlerta = idAlerta; }

    public Integer getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(Integer idEstudiante) { this.idEstudiante = idEstudiante; }

    public Integer getIdNota() { return idNota; }
    public void setIdNota(Integer idNota) { this.idNota = idNota; }

    public String getAsignatura() { return asignatura; }
    public void setAsignatura(String asignatura) { this.asignatura = asignatura; }

    public Double getCalificacion() { return calificacion; }
    public void setCalificacion(Double calificacion) { this.calificacion = calificacion; }

    public String getNivelAlerta() { return nivelAlerta; }
    public void setNivelAlerta(String nivelAlerta) { this.nivelAlerta = nivelAlerta; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getFechaAlerta() { return fechaAlerta; }
    public void setFechaAlerta(String fechaAlerta) { this.fechaAlerta = fechaAlerta; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String idAlerta;
        private Integer idEstudiante;
        private Integer idNota;
        private String asignatura;
        private Double calificacion;
        private String nivelAlerta;
        private String mensaje;
        private String fechaAlerta;

        public Builder idAlerta(String idAlerta) { this.idAlerta = idAlerta; return this; }
        public Builder idEstudiante(Integer idEstudiante) { this.idEstudiante = idEstudiante; return this; }
        public Builder idNota(Integer idNota) { this.idNota = idNota; return this; }
        public Builder asignatura(String asignatura) { this.asignatura = asignatura; return this; }
        public Builder calificacion(Double calificacion) { this.calificacion = calificacion; return this; }
        public Builder nivelAlerta(String nivelAlerta) { this.nivelAlerta = nivelAlerta; return this; }
        public Builder mensaje(String mensaje) { this.mensaje = mensaje; return this; }
        public Builder fechaAlerta(String fechaAlerta) { this.fechaAlerta = fechaAlerta; return this; }

        public AlertaAcademica build() {
            return new AlertaAcademica(idAlerta, idEstudiante, idNota, asignatura, calificacion, nivelAlerta, mensaje, fechaAlerta);
        }
    }
}
