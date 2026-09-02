package cl.duoc.secciones_service.client;

import cl.duoc.secciones_service.dto.IntegranteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class IntegranteClient {

    @Autowired
    private WebClient webClient;

public IntegranteDTO obtenerIntegrante(Integer id){

        return webClient
                .get()
                .uri("/api/v1/integrantes/{id}" , id)
                .retrieve()
                .bodyToMono(IntegranteDTO.class)
                .block();
    }
}
