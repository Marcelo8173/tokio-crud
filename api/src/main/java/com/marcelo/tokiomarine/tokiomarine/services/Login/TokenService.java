package com.marcelo.tokiomarine.tokiomarine.services.Login;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.marcelo.tokiomarine.tokiomarine.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        try {
            UUID userId = user.getId();

            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(user.getEmail())
                    .withClaim("role", user.getTypeUser().name())
                    .withClaim("userId", userId.toString())
                    .withExpiresAt(generateExpirateDate())
                    .sign(algorithm)
                    ;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error during generate token", exception);

        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    private Instant generateExpirateDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    public UUID extractUserId(String auth) {
        String token = auth.replace("Bearer ", ""); // Remove "Bearer "
        DecodedJWT decodedJWT = JWT.decode(token);
        return UUID.fromString(decodedJWT.getClaim("userId").asString());
    }

}