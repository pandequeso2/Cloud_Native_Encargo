package cl.duoc.entregas_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "entregas")
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "idEntrega")
    private Integer idEntrega;

    @Column (name = "idGrupo")
    private Integer idGrupo;

    @Column (name = "idTrabajo")
    private Integer idTrabajo;

    @Column (name = "fechaEntrega")
    private LocalDate fechaEntrega;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    public enum Estado {
        PENDIENTE, ENTREGADO, ATRASADO
    }
}