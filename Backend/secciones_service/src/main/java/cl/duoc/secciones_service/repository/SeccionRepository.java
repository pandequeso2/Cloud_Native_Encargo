package cl.duoc.secciones_service.repository;

import cl.duoc.secciones_service.model.Seccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeccionRepository extends JpaRepository<Seccion, Integer> {

    List<Seccion> findByIdAsignatura(Integer idAsignatura);

    List<Seccion> findByIdProfesor(Integer idProfesor);
}
