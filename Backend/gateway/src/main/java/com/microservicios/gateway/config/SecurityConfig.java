package com.microservicios.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
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

    // Endpoints públicos: documentación y health check.
    // Todo lo demás (todas las rutas /api/v1/**) exige un JWT válido de Entra ID. j
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
                        .pathMatchers(PUBLIC_PATHS).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(entraJwtAuthenticationConverter()))
                );
        return http.build();
    }

    /**
     * Entra ID entrega los roles de aplicación (App Roles) en el claim "roles"
     * y los permisos delegados en el claim "scp". Mapeamos ambos a
     * GrantedAuthority para poder usar @PreAuthorize("hasRole('...')") o
     * reglas por ruta más adelante si se requiere autorización fina.
     */
    private Converter<Jwt, Mono<AbstractAuthenticationToken>> entraJwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter rolesConverter = new JwtGrantedAuthoritiesConverter();
        rolesConverter.setAuthorityPrefix("ROLE_");
        rolesConverter.setAuthoritiesClaimName("roles");

        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();
        scopesConverter.setAuthorityPrefix("SCOPE_");
        scopesConverter.setAuthoritiesClaimName("scp");

        Converter<Jwt, Collection<GrantedAuthority>> combinedAuthorities = jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.addAll(rolesConverter.convert(jwt));
            authorities.addAll(scopesConverter.convert(jwt));
            return authorities;
        };

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(combinedAuthorities);

        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Ajustar al/los origen(es) real(es) donde correrá el frontend React (dev y prod)
        config.setAllowedOriginPatterns(List.of("http://localhost:3000", "https://*.tudominio.cl"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}