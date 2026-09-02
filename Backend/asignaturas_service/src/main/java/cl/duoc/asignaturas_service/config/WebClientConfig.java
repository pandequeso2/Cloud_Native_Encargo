package cl.duoc.asignaturas_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean("asignaturaWebClient")
    public WebClient asignaturaWebClient(){
        return WebClient.builder()
                .baseUrl("http://localhost:8084")
                .build();
    }

    @Bean("seccionWebClient")
    public WebClient seccionWebClient() {
        return WebClient.builder()
                .baseUrl("http://secciones-service:8089")
                .build();
    }
}
