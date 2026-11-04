package cl.duoc.comentarios_service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Modelo de Evento JSON para el Tópico 2 ("comentarios-academicos-topic").
 * Cumple con el estándar del Plan de Trabajo: (id, entregaId, usuarioId, textoComentario, fechaCreacion).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComentarioEvento implements Serializable {

    @JsonProperty("id")
    @JsonAlias({"idComentario"})
    private Integer id;

    @JsonProperty("entregaId")
    @JsonAlias({"idEntrega"})
    private Integer entregaId;

    @JsonProperty("usuarioId")
    @JsonAlias({"idProfesor", "idUsuario"})
    private Integer usuarioId;

    @JsonProperty("textoComentario")
    @JsonAlias({"contenido"})
    private String textoComentario;

    @JsonProperty("fechaCreacion")
    @JsonAlias({"fechaComentario"})
    private String fechaCreacion;

    private String tipoComentario;

    // Métodos alias para compatibilidad bidireccional
    @JsonProperty("idComentario")
    public Integer getIdComentario() { return this.id; }
    public void setIdComentario(Integer idComentario) { this.id = idComentario; }

    @JsonProperty("idEntrega")
    public Integer getIdEntrega() { return this.entregaId; }
    public void setIdEntrega(Integer idEntrega) { this.entregaId = idEntrega; }

    @JsonProperty("idProfesor")
    public Integer getIdProfesor() { return this.usuarioId; }
    public void setIdProfesor(Integer idProfesor) { this.usuarioId = idProfesor; }

    @JsonProperty("contenido")
    public String getContenido() { return this.textoComentario; }
    public void setContenido(String contenido) { this.textoComentario = contenido; }

    @JsonProperty("fechaComentario")
    public String getFechaComentario() { return this.fechaCreacion; }
    public void setFechaComentario(String fechaComentario) { this.fechaCreacion = fechaComentario; }
}
