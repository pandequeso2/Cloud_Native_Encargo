package com.microservicios.gateway.filter;
 
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
 
/**
 * Una vez que el JWT fue validado por el Gateway (BFF), este filtro reenvía j
 * la identidad del usuario hacia los microservicios internos mediante
 * headers. Los microservicios NO vuelven a validar el JWT: confían en la red
 * interna y en que solo el Gateway puede llegar a ellos.
 *
 * Si en el futuro los microservicios quedan expuestos directamente (sin
 * pasar por el Gateway), esto deja de ser seguro y cada uno debería validar
 * el JWT también.
 */
@Component
public class UserContextForwardingFilter implements GlobalFilter, Ordered {
 
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .cast(JwtAuthenticationToken.class)
                .map(auth -> {
                    String email = auth.getToken().getClaimAsString("preferred_username");
                    String roles = auth.getAuthorities().stream()
                            .map(Object::toString)
                            .reduce((a, b) -> a + "," + b)
                            .orElse("");
 
                    ServerWebExchange mutated = exchange.mutate()
                            .request(r -> r
                                    .header("X-User-Email", email != null ? email : "")
                                    .header("X-User-Roles", roles))
                            .build();
                    return mutated;
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }