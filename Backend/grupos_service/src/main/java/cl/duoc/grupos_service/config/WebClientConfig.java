package cl.duoc.grupos_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://integrantes-service:8087")
                .build();
    }

    @Bean("trabajoWebClient")
    public WebClient trabajoWebClient() {
        return WebClient.builder()
                .baseUrl("http://trabajos-service:8090")
                .build();
    }
}