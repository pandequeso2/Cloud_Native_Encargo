

CREATE TABLE asignaturas (
                             idAsignatura INT AUTO_INCREMENT,
                             nombreAsignatura VARCHAR(25) NOT NULL,
                             idProfesor INT NOT NULL,
                             CONSTRAINT PK_asignaturas PRIMARY KEY (idAsignatura)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;