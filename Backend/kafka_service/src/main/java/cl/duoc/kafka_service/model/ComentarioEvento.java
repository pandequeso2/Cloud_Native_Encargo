package cl.duoc.kafka_service.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Modelo de Evento JSON para el Tópico 2 ("comentarios-academicos-topic").
 * Cumple con el estándar del Plan de Trabajo: (id, entregaId, usuarioId, textoComentario, fechaCreacion).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComentarioEvento implements Serializable {

    @JsonAlias({"idComentario"})
    private Integer id;

    @JsonAlias({"idEntrega"})
    private Integer entregaId;

    @JsonAlias({"idProfesor", "idUsuario"})
    private Integer usuarioId;

    @JsonAlias({"contenido"})
    private String textoComentario;

    @JsonAlias({"fechaComentario"})
    private String fechaCreacion;

    private String tipoComentario;

    public ComentarioEvento() {}

    public ComentarioEvento(Integer id, Integer entregaId, Integer usuarioId, String textoComentario, String fechaCreacion, String tipoComentario) {
        this.id = id;
        this.entregaId = entregaId;
        this.usuarioId = usuarioId;
        this.textoComentario = textoComentario;
        this.fechaCreacion = fechaCreacion;
        this.tipoComentario = tipoComentario;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getEntregaId() { return entregaId; }
    public void setEntregaId(Integer entregaId) { this.entregaId = entregaId; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getTextoComentario() { return textoComentario; }
    public void setTextoComentario(String textoComentario) { this.textoComentario = textoComentario; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getTipoComentario() { return tipoComentario; }
    public void setTipoComentario(String tipoComentario) { this.tipoComentario = tipoComentario; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer entregaId;
        private Integer usuarioId;
        private String textoComentario;
        private String fechaCreacion;
        private String tipoComentario;

        public Builder id(Integer id) { this.id = id; return this; }
        public Builder entregaId(Integer entregaId) { this.entregaId = entregaId; return this; }
        public Builder usuarioId(Integer usuarioId) { this.usuarioId = usuarioId; return this; }
        public Builder textoComentario(String textoComentario) { this.textoComentario = textoComentario; return this; }
        public Builder fechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; return this; }
        public Builder tipoComentario(String tipoComentario) { this.tipoComentario = tipoComentario; return this; }

        public ComentarioEvento build() {
            return new ComentarioEvento(id, entregaId, usuarioId, textoComentario, fechaCreacion, tipoComentario);
        }
    }
}

