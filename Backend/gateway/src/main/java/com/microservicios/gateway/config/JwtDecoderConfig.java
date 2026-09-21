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

        // 2. Validamos explícitamente el emisor sts.windows.net que envía Entra ID v1
        String expectedIssuer = "https://sts.windows.net/" + tenantId + "/";
        OAuth2TokenValidator<Jwt> issuerValidator = new JwtIssuerValidator(expectedIssuer);
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
                    aud.replace("api://", "").equals(expectedAudience.replace("api://", ""))
                );
                
                if (matches) {
                    return OAuth2TokenValidatorResult.success();
                }
            }
            
            OAuth2Error error = new OAuth2Error(
                    "invalid_token",
                    "El token no contiene el audience esperado: " + expectedAudience,
                    null
            );
            return OAuth2TokenValidatorResult.failure(error);
        }
    }
}