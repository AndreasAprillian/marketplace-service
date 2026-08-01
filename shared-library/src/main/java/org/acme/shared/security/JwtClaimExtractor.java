package org.acme.shared.security;

import lombok.Getter;
import lombok.Setter;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;

public class JwtClaimExtractor {

    private static final String SECRET = getSecret();

    public static ExtractedClaims extract(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = authorizationHeader.substring(7);
        return extractFromToken(token);
    }

    public static ExtractedClaims extractFromToken(String token) {
        try {
            var consumer = new JwtConsumerBuilder()
                    .setRequireExpirationTime()
                    .setAllowedClockSkewInSeconds(30)
                    .setVerificationKey(new HmacKey(SECRET.getBytes()))
                    .setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, AlgorithmIdentifiers.HMAC_SHA256)
                    .build();

            JwtClaims claims = consumer.processToClaims(token);

            ExtractedClaims result = new ExtractedClaims();
            result.setCustomerId(claims.getClaimValue("customerId", Long.class));
            result.setUsername(claims.getClaimValue("username", String.class));
            result.setEmail(claims.getClaimValue("email", String.class));
            result.setRegion(claims.getClaimValue("region", String.class));
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired JWT token", e);
        }
    }

    private static String getSecret() {
        String secret = System.getenv("MP_JWT_VERIFY_PUBLICKEY");
        return secret != null ? secret : "marketplace-secret-key-change-in-production-minimum-256-bits";
    }

    @Getter
    @Setter
    public static class ExtractedClaims {
        private Long customerId;
        private String username;
        private String email;
        private String region;
    }
}
