package cl.duoc.entregas_service.repository;

import cl.duoc.entregas_service.model.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, Integer> {
}