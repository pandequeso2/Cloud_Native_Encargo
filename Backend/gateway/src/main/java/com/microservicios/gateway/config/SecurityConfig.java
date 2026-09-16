package com.microservicios.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {
            "/actuator/health",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**",
            "/api/v1/*/v3/api-docs/**"
    };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(PUBLIC_PATHS).permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/grupos/**").hasRole("Admin")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/grupos/**").hasRole("Admin")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/grupos/**").hasRole("Admin")
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(entraJwtAuthenticationConverter()))
                );
        return http.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> entraJwtAuthenticationConverter() {
        Converter<Jwt, Collection<GrantedAuthority>> customAuthoritiesConverter = jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            String email = jwt.getClaimAsString("preferred_username");
            if (email == null || email.isBlank()) {
                email = jwt.getClaimAsString("email");
            }

            String rol = resolverRolPorEmail(email);
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol));

            return authorities;
        };

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(customAuthoritiesConverter);

        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }

    private String resolverRolPorEmail(String email) {
        if (email == null) {
            return "Estudiante";
        }

        String emailLower = email.toLowerCase().trim();

        if (emailLower.equals("ben.arayag@duocuc.cl")) {
            return "Admin";
        } else if (emailLower.equals("vi.garridod@duocuc.cl")) {
            return "Profesor";
        } else if (emailLower.equals("mat.mirandag@duocuc.cl")) {
            return "Estudiante";
        }

        return "Estudiante";
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        String allowedOrigin = System.getenv().getOrDefault("ALLOWED_ORIGIN", "http://localhost:3000");
        
        config.setAllowedOriginPatterns(List.of(
                allowedOrigin,
                "http://localhost:3000",
                "http://localhost:5173",
                "http://54.235.56.150"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}