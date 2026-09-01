
CREATE TABLE profesores (
                            idProfesor        INT NOT NULL AUTO_INCREMENT,
                            nombreProfesor    VARCHAR(100) NOT NULL,
                            apellidoProfesor  VARCHAR(100) NOT NULL,
                            rutCuerpo         INT NOT NULL,
                            rutDv             VARCHAR(1) NOT NULL,
                            correoElectronico VARCHAR(50) NOT NULL,
                            PRIMARY KEY (idProfesor)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;