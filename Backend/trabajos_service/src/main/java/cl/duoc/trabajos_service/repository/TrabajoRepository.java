package cl.duoc.trabajos_service.repository;

import cl.duoc.trabajos_service.model.Trabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrabajoRepository extends JpaRepository<Trabajo, Integer> {

    Trabajo findByIdGrupo(Integer idGrupo);
}