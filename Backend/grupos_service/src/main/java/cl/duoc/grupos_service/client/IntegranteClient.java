package cl.duoc.grupos_service.client;

import cl.duoc.grupos_service.dto.IntegranteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Service
public class IntegranteClient {

    @Autowired
    private WebClient webClient;

    public IntegranteDTO obtenerIntegrante(Integer id) {
        return webClient
                .get()
                .uri("/api/v1/integrantes/{id}", id)
                .retrieve()
                .bodyToMono(IntegranteDTO.class)
                .block();
    }

    public List<IntegranteDTO> obtenerPorGrupo(Integer idGrupo) {
        return webClient
                .get()
                .uri("/api/v1/integrantes/grupo/{idGrupo}", idGrupo)
                .retrieve()
                .bodyToFlux(IntegranteDTO.class)
                .collectList()
                .block();
    }
}