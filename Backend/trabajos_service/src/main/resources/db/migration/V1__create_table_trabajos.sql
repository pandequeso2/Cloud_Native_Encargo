
CREATE TABLE trabajos (
                          idTrabajo      INT NOT NULL AUTO_INCREMENT,
                          nombreTrabajo  VARCHAR(100) NOT NULL,
                          porcentajeNota DOUBLE NOT NULL,
                          idGrupo        INT NOT NULL,
                          tipoTrabajo    ENUM('ENCARGO', 'PRESENTACION', 'OTRO') NOT NULL,
                          semestre       INT NOT NULL,
                          estado         ENUM('PENDIENTE', 'ENTREGADO', 'EVALUADO') NOT NULL,
                          PRIMARY KEY (idTrabajo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;