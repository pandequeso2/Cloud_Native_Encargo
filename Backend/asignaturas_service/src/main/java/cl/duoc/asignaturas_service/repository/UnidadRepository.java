package cl.duoc.asignaturas_service.repository;

import cl.duoc.asignaturas_service.model.Unidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnidadRepository extends JpaRepository<Unidad, Integer> {
}
