package cl.duoc.roles_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRol;

    @Column (name = "rol")
    @NotBlank(message = "El nombre del rol no puede estar vacio")
    private String rol;
}