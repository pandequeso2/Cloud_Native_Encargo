package cl.duoc.comentarios_service.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comentarios")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idComentario")
    @JsonAlias({"id"})
    private Integer idComentario;

    @Column(name = "idEntrega")
    @JsonAlias({"entregaId"})
    private Integer idEntrega;

    @Column(name = "idProfesor")
    @JsonAlias({"usuarioId", "idUsuario"})
    private Integer idProfesor;

    @Column(name = "contenido")
    @JsonAlias({"textoComentario"})
    private String contenido;

    @Column(name = "fechaComentario")
    @JsonAlias({"fechaCreacion"})
    private LocalDateTime fechaComentario;

    @Enumerated(EnumType.STRING)
    private TipoComentario tipoComentario;

    @JsonProperty("id")
    public Integer getId() {
        return this.idComentario;
    }

    @JsonProperty("entregaId")
    public Integer getEntregaId() {
        return this.idEntrega;
    }

    @JsonProperty("usuarioId")
    public Integer getUsuarioId() {
        return this.idProfesor;
    }

    @JsonProperty("textoComentario")
    public String getTextoComentario() {
        return this.contenido;
    }

    @JsonProperty("fechaCreacion")
    public String getFechaCreacion() {
        return this.fechaComentario != null ? this.fechaComentario.toString() : null;
    }

    public enum TipoComentario {
        CORRECION, FELICITACION, CONSULTA
    }
}
