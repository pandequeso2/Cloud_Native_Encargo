package cl.duoc.comentarios_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idComentario")
    private Integer idComentario;

    @Column(name = "idEntrega")
    private Integer idEntrega;

    @Column(name = "idProfesor")
    private Integer idProfesor;

    @Column(name = "contenido")
    private String contenido;

    @Column(name = "fechaComentario")
    private LocalDateTime fechaComentario;

    @Enumerated(EnumType.STRING)
    private TipoComentario tipoComentario;

    public enum TipoComentario {
        CORRECION, FELICITACION, CONSULTA
    }
}