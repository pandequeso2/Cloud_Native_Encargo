package cl.duoc.grupos_service.client;

import cl.duoc.grupos_service.dto.TrabajoDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TrabajoClient {

    private final WebClient webClient;

    public TrabajoClient(@Qualifier("trabajoWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public TrabajoDTO obtenerPorGrupo(Integer idGrupo) {
        return webClient.get()
                .uri("/api/v1/trabajos/grupo/{idGrupo}", idGrupo)
                .retrieve()
                .bodyToMono(TrabajoDTO.class)
                .block();
    }
}