package cl.duoc.comentarios_service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
}

