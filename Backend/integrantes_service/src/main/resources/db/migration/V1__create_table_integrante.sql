
CREATE TABLE integrantes (
                             idIntegrante       INT NOT NULL AUTO_INCREMENT,
                             nombre             VARCHAR(25) NOT NULL,
                             apellido           VARCHAR(25) NOT NULL,
                             rutCuerpo          INT(8) NOT NULL,
                             rutDv              VARCHAR(1) NOT NULL,
                             correoElectronico  VARCHAR(50) NOT NULL,
                             idRol              INT(1) NOT NULL,
                             idGrupo            INT(1) NOT NULL,
                             disponibilidad     ENUM('BAJA', 'MEDIA', 'ALTA') NOT NULL,
                             idNota             INT(1) NOT NULL,
                             PRIMARY KEY (idIntegrante)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;