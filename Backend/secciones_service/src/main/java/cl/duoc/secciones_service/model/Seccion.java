package cl.duoc.secciones_service.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "secciones")
@Data
@JsonPropertyOrder({
        "idSeccion",
        "codigoSeccion",
        "capacidadMaxima"
})
public class Seccion {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSeccion;

    @Column(name = "codigoSeccion")
    @NotBlank(message = "el código de la sección no puede estar vacio")
    private String codigoSeccion;

    @Column(name = "capacidadMaxima")
    @NotNull(message = "la capacidad maxima es obligatoria")
    private Integer capacidadMaxima;

    @Column(name = "idAsignatura")
    @NotNull(message = "el ID de la asignatura es obligatorio")
    private Integer idAsignatura;

    @Column(name = "idProfesor")
    @NotNull(message = "el ID del profesor es obligatorio")
    private Integer idProfesor;
}
