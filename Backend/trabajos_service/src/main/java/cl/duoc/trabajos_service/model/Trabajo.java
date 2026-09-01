package cl.duoc.trabajos_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "trabajos")
public class Trabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTrabajo;

    @Column(name = "nombreTrabajo")
    @NotBlank(message = "el nombre del trabajo no puede estar vacío")
    private String nombreTrabajo;

    @Column(name = "porcentajeNota")
    @NotNull(message = "el porcentaje de la nota es obligatorio")
    private Double porcentajeNota;

    @Column(name = "idGrupo")
    @NotNull(message = "el ID del grupo es obligatorio")
    private Integer idGrupo;

    @NotNull(message = "el tipo de trabajo es obligatorio")
    @Enumerated(EnumType.STRING)
    private TipoTrabajo tipoTrabajo;

    @Column(name = "semestre")
    @NotNull(message = "el semestre es obligatorio")
    private Integer semestre;

    @NotNull(message = "el estado es obligatorio")
    @Enumerated(EnumType.STRING)
    private Estado estado;

    public enum TipoTrabajo {
        ENCARGO, PRESENTACION, OTRO
    }

    public enum Estado {
        PENDIENTE, ENTREGADO, EVALUADO
    }
}