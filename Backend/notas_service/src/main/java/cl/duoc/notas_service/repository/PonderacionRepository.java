package cl.duoc.notas_service.repository;

import cl.duoc.notas_service.model.Ponderacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PonderacionRepository extends JpaRepository<Ponderacion, Integer> {
}
