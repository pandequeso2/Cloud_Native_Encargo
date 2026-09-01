package cl.duoc.asignaturas_service.client;

import cl.duoc.asignaturas_service.dto.AsignaturaDTO;
import cl.duoc.asignaturas_service.model.Asignatura;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class AsignaturaClient {

    private final WebClient webClient;

    public AsignaturaClient(@Qualifier("asignaturaWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public List<AsignaturaDTO> obtenerAsignaturas() {
        return webClient.get()
                .uri("/api/v1/asignaturas/{id}")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AsignaturaDTO>>() {})
                .block();
    }
}
