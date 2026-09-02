package cl.duoc.integrantes_service.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "integrantes")
@JsonPropertyOrder({
        "idIntegrante",
        "nombre",
        "apellido",
        "rutCuerpo",
        "rutDv",
        "correoElectronico",
        "idRol",
        "idGrupo",
        "disponibilidad",
        "idNota"
})
public class Integrante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idIntegrante")
    private Integer idIntegrante;

    @NotBlank(message = "el nombre es obligatorio")
    @Column(name = "nombre")
    private String nombre;

    @NotBlank(message = "el apellido es obligatorio")
    @Column(name = "apellido")
    private String apellido;

    @NotNull(message = "el rut es obligatorio")
    @Column(name = "rutCuerpo")
    private Integer rutCuerpo;

    @NotBlank(message = "el digito verificador es obligatorio")
    @Column(name = "rutDv")
    private String rutDv;

    @NotBlank(message = "el correo electronico es obligatorio")
    @Column(name = "correoElectronico")
    private String correoElectronico;

    @NotNull(message = "el ID de Rol es obligatorio")
    @Column(name = "idRol")
    private Integer idRol;

    @NotNull(message = "el ID de Grupo es obligatorio")
    @Column(name = "idGrupo")
    private Integer idGrupo;

    @NotNull(message = "la disponibilidad es obligatoria")
    @Enumerated(EnumType.STRING)
    private Disponibilidad disponibilidad;

    @NotNull(message = "el ID de Nota es obligatorio")
    @Column(name = "idNota")
    private Integer idNota;

    public enum Disponibilidad {
        BAJA, MEDIA, ALTA
    }
}