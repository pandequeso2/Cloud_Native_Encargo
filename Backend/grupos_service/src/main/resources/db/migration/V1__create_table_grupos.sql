
CREATE TABLE grupos (
                        idGrupo         INT NOT NULL AUTO_INCREMENT,
                        nombreGrupo     VARCHAR(100) NOT NULL,
                        capacidadMaxima INT NOT NULL,
                        fechaCreacion   DATE NOT NULL,
                        grupoLleno      TINYINT(1) NOT NULL,
                        PRIMARY KEY (idGrupo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;