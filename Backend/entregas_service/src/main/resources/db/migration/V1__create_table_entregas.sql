
CREATE TABLE entregas (
                          idEntrega     INT NOT NULL AUTO_INCREMENT,
                          idGrupo       INT NOT NULL,
                          idTrabajo     INT NOT NULL,
                          fechaEntrega  DATE NOT NULL,
                          estado        VARCHAR(20) NOT NULL,
                          PRIMARY KEY (idEntrega)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;