package cl.duoc.integrantes_service.repository;

import cl.duoc.integrantes_service.model.Integrante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntegranteRepository extends JpaRepository<Integrante, Integer> {

    List<Integrante> findByIdGrupo(Integer idGrupo);

    List<Integrante> findByDisponibilidad(Integrante.Disponibilidad disponibilidad);

    public interface integranteRepository extends JpaRepository<Integrante, Integer> {
        List<Integrante> findByIdGrupo(Integer idGrupo);
    }

}