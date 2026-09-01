package cl.duoc.asignaturas_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "asignaturas")
@Data
public class Asignatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAsignatura")
    private Integer idAsignatura;

    @Column(name = "nombreAsignatura")
    @NotBlank(message = "el nombre de la asignatura no puede estar vacio")
    private String nombreAsignatura;

    @Column(name = "idProfesor")
    @NotNull(message = "el ID del profesor es obligatorio")
    private Integer idProfesor;
}