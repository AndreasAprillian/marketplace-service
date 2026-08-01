package org.acme.customer.security;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.customer.entity.CustomerEntity;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;

@ApplicationScoped
public class JwtTokenProvider {

    public String generateToken(CustomerEntity customer) {
        try {
            JwtClaims claims = new JwtClaims();
            claims.setIssuer("marketplace");
            claims.setSubject(customer.getUsername());
            claims.setClaim("customerId", customer.getId());
            claims.setClaim("username", customer.getUsername());
            claims.setClaim("email", customer.getEmail());
            claims.setClaim("region", customer.getRegion());
            claims.setExpirationTimeMinutesInTheFuture(1440);

            JsonWebSignature jws = new JsonWebSignature();
            jws.setPayload(claims.toJson());
            jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
            jws.setKey(new HmacKey(getSecretKeyBytes()));

            return jws.getCompactSerialization();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate token", e);
        }
    }

    private byte[] getSecretKeyBytes() {
        String secret = System.getenv("MP_JWT_VERIFY_PUBLICKEY");
        return secret != null ? secret.getBytes() : "loremipsumdolorsitametconsecteturadipiscingelit".getBytes();
    }
}
