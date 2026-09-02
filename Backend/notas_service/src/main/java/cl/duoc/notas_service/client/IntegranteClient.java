package cl.duoc.notas_service.client;

import cl.duoc.notas_service.dto.IntegranteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class IntegranteClient {

    @Autowired
    private WebClient webClient;

    public IntegranteDTO obtenerIntegrante(Integer id) {
        return webClient.get()
                .uri("http://integrantes-service:8087/api/v1/integrantes/" + id)
                .retrieve()
                .bodyToMono(IntegranteDTO.class)
                .block();
    }
}
