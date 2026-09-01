package cl.duoc.integrantes_service.client;

import cl.duoc.integrantes_service.dto.RolDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RolClient {

    private final WebClient webClient;

    public RolClient(@Qualifier("rolWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public RolDTO obtenerRol(Integer idRol) {
        return webClient.get()
                .uri("/api/v1/roles/{idRol}", idRol)
                .retrieve()
                .bodyToMono(RolDTO.class)
                .block();
    }
}