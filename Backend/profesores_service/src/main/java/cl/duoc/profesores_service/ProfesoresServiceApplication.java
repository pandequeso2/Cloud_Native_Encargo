package cl.duoc.profesores_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(servers = {@Server(url = "/", description = "Ruta a través del Gateway")})
@SpringBootApplication
@EnableDiscoveryClient
public class ProfesoresServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProfesoresServiceApplication.class, args);
	}

}
