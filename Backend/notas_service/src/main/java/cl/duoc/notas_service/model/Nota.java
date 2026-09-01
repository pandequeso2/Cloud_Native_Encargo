package cl.duoc.notas_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "notas")
@Data
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNota;

    @Column(name = "nota")
    @NotNull(message = "la nota es obligatoria")
    private Double nota;

    @ManyToOne
    @JoinColumn(name = "idPonderacion", nullable = false)
    private Ponderacion ponderacion;

    @Column(name = "idIntegrante")
    @NotNull(message = "el id del integrante es obligatorio")
    private Integer idIntegrante;

}