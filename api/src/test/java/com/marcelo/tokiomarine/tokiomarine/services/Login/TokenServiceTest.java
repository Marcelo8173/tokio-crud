package com.marcelo.tokiomarine.tokiomarine.services.Login;
import com.marcelo.tokiomarine.tokiomarine.domain.User;
import com.marcelo.tokiomarine.tokiomarine.domain.enums.TypeUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {
    private TokenService tokenService;
    private final String secret = "test-secret-key";

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", secret);
    }

    @Test
    @DisplayName(value = "It should be able return a jwt")
    void generateToken_ShouldReturnValidJWT_WhenUserIsValid() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setTypeUser(TypeUser.USUARIO_COMUM);

        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String subject = tokenService.validateToken(token);
        assertEquals("test@example.com", subject);
    }

    @Test
    @DisplayName(value = "It should be able validate a jwt")
    void validateToken_ShouldReturnSubject_WhenTokenIsValid() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("valid@example.com");
        user.setTypeUser(TypeUser.USUARIO_COMUM);

        String token = tokenService.generateToken(user);
        String subject = tokenService.validateToken(token);

        assertEquals("valid@example.com", subject);
    }

    @Test
    @DisplayName(value = "It should be able return empty valeu when jwt is invalid")
    void validateToken_ShouldReturnEmptyString_WhenTokenIsInvalid() {
        String invalidToken = "invalid.token.string";

        String result = tokenService.validateToken(invalidToken);

        assertEquals("", result);
    }

    @Test
    @DisplayName(value = "It should be able extract user_id")
    void extractUserId_ShouldReturnCorrectUUID() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setTypeUser(TypeUser.USUARIO_COMUM);

        String token = tokenService.generateToken(user);
        String authHeader = "Bearer " + token;

        UUID extractedId = tokenService.extractUserId(authHeader);

        assertEquals(userId, extractedId);
    }
}