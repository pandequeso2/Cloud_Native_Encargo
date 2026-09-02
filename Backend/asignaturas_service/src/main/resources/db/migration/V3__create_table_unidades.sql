
CREATE TABLE unidades (
                          idUnidad      INT NOT NULL AUTO_INCREMENT,
                          idAsignatura  INT NOT NULL,
                          numeroUnidad  INT NOT NULL,
                          nombreUnidad  VARCHAR(100) NOT NULL,
                          PRIMARY KEY (idUnidad)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;