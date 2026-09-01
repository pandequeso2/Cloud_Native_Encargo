
CREATE TABLE secciones (
                           idSeccion       INT NOT NULL AUTO_INCREMENT,
                           codigoSeccion   VARCHAR(20) NOT NULL,
                           capacidadMaxima INT NOT NULL,
                           idAsignatura    INT NOT NULL,
                           idProfesor      INT NOT NULL,
                           PRIMARY KEY (idSeccion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;