
CREATE TABLE notas (
                       idNota          INT NOT NULL AUTO_INCREMENT,
                       nota            DOUBLE NOT NULL,
                       idPonderacion   INT NOT NULL,
                       idIntegrante    INT NOT NULL,
                       PRIMARY KEY (idNota),
                       FOREIGN KEY (idPonderacion) REFERENCES ponderaciones(idPonderacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;