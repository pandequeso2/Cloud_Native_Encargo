package cl.duoc.profesores_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "profesores")
@Data
@JsonPropertyOrder({
        "idProfesor",
        "nombreProfesor",
        "apellidoProfesor",
        "rutCuerpo",
        "rutDv",
        "correoElectronico"
})
public class Profesor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProfesor")
    private Integer idProfesor;

    @Column(name = "nombreProfesor")
    @NotBlank(message = "El nombre del profesor no puede estar vacío")
    private String nombreProfesor;

    @Column(name = "apellidoProfesor")
    @NotBlank(message = "El apellido del profesor no puede estar vacío")
    private String apellidoProfesor;

    @Column(name = "rutCuerpo")
    @NotNull(message = "El cuerpo del RUT es obligatorio")
    private Integer rutCuerpo;

    @Column(name = "rutDv")
    @NotBlank(message = "El dígito verificador del RUT no puede estar vacío")
    private String rutDv;

    @Column(name = "correoElectronico")
    @NotBlank(message = "El correo electrónico no puede estar vacío")
    private String correoElectronico;
}