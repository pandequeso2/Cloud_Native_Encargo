package cl.duoc.asignaturas_service.client;

import cl.duoc.asignaturas_service.dto.SeccionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class SeccionClient {

    private final WebClient webClient;

    public SeccionClient(@Qualifier("seccionWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public List<SeccionDTO> obtenerPorAsignatura(Integer idAsignatura) {
        return webClient.get()
                .uri("/api/v1/secciones/asignatura/{idAsignatura}", idAsignatura)
                .retrieve()
                .bodyToFlux(SeccionDTO.class)
                .collectList()
                .block();
    }
}
