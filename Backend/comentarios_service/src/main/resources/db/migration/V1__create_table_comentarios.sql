
CREATE TABLE comentarios (
                             idComentario     INT NOT NULL AUTO_INCREMENT,
                             idEntrega        INT NOT NULL,
                             idProfesor       INT NOT NULL,
                             contenido        VARCHAR(500) NOT NULL,
                             fechaComentario  DATETIME NOT NULL,
                             tipoComentario   VARCHAR(20) NOT NULL,
                             PRIMARY KEY (idComentario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;