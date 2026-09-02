package cl.duoc.integrantes_service.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean("rolWebClient")
    public WebClient rolWebClient() {
        return WebClient.builder()
                .baseUrl("http://roles-service:8088")
                .build();
    }
}