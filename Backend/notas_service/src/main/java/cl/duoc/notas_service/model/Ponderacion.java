package cl.duoc.notas_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "ponderaciones")
@Data
public class Ponderacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPonderacion;

    @Column(name = "valorPorcentaje")
    @NotNull(message = "el valor del porcentaje es obligatorio")
    private Double porcentaje;

    @JsonIgnore
    @OneToMany(mappedBy = "ponderacion", cascade = CascadeType.ALL)
    private List<Nota> notas;
}
