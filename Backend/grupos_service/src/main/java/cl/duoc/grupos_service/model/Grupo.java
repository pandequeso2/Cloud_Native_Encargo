package cl.duoc.grupos_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "grupos")
@Data
public class Grupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idGrupo;

    @Column(name = "nombreGrupo")
    @NotBlank(message = "el nombre del grupo no puede estar vacio")
    private String nombreGrupo;

    @Column(name = "capacidadMaxima")
    @NotNull(message = "la capacidad maxima es obligatoria")
    private Integer capacidadMaxima;

    @Column(name = "fechaCreacion")
    @NotNull(message = "la fecha de creacion es obligatoria")
    private LocalDate fechaCreacion;

    @Column(name = "grupoLleno")
    @NotNull(message = "el estado del grupo es obligatorio")
    private Boolean grupoLleno;
}