package com.microservicios.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;

@Configuration
public class JwtDecoderConfig {

    @Value("${security.entra-id.tenant-id}")
    private String tenantId;

    @Value("${security.entra-id.audience}")
    private String audience;

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        // 1. Apuntamos directamente a las llaves públicas de Microsoft en login.microsoftonline.com
        String jwkSetUri = "https://login.microsoftonline.com/common/discovery/v2.0/keys";
        
        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder
                .withJwkSetUri(jwkSetUri)
                .build();

        // 2. Validamos el emisor de Entra ID (tanto v1 como v2)
        OAuth2TokenValidator<Jwt> issuerValidator = jwt -> {
            if (jwt.getIssuer() != null) {
                String iss = jwt.getIssuer().toString();
                if (iss.contains(tenantId) || iss.contains("microsoftonline.com") || iss.contains("windows.net")) {
                    return OAuth2TokenValidatorResult.success();
                }
            }
            return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "El emisor del token no es válido: " + jwt.getIssuer(), null));
        };

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefault(),
                issuerValidator,
                audienceValidator
        ));

        return decoder;
    }

    static class AudienceValidator implements OAuth2TokenValidator<Jwt> {

        private final String expectedAudience;

        AudienceValidator(String expectedAudience) {
            this.expectedAudience = expectedAudience;
        }

        @Override
        public OAuth2TokenValidatorResult validate(Jwt jwt) {
            if (jwt.getAudience() != null) {
                boolean matches = jwt.getAudience().stream().anyMatch(aud -> 
                    aud.equals(expectedAudience) || 
                    aud.replace("api://", "").equals(expectedAudience.replace("api://", "")) ||
                    expectedAudience.contains(aud) ||
                    aud.contains("cdde4875-2f04-4a9e-b27c-2e1ad93c9366") ||
                    aud.contains("be1f94f2-51ba-4a74-ab8d-b258e2123e6e")
                );
                
                if (matches) {
                    return OAuth2TokenValidatorResult.success();
                }
            }
            
            OAuth2Error error = new OAuth2Error(
                    "invalid_token",
                    "El token no contiene el audience esperado: " + expectedAudience + " (recibido: " + jwt.getAudience() + ")",
                    null
            );
            return OAuth2TokenValidatorResult.failure(error);
        }
    }
}