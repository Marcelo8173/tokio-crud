package com.marcelo.tokiomarine.tokiomarine.services.Login;

import com.marcelo.tokiomarine.tokiomarine.DTOs.AuthenticationDTO;
import com.marcelo.tokiomarine.tokiomarine.domain.User;
import com.marcelo.tokiomarine.tokiomarine.domain.enums.TypeUser;
import com.marcelo.tokiomarine.tokiomarine.domain.login.CustomUserDetails;
import com.marcelo.tokiomarine.tokiomarine.services.Login.LoginService;
import com.marcelo.tokiomarine.tokiomarine.services.Login.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceTest {
    private TokenService tokenService;
    private AuthenticationManager authenticationManager;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        tokenService = mock(TokenService.class);
        authenticationManager = mock(AuthenticationManager.class);
        loginService = new LoginService(tokenService, authenticationManager);
    }

    @Test
    @DisplayName(value = "It should be able return token when login is valid")
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        String email = "test@example.com";
        String senha = "StrongPass123!";
        String fakeToken = "fake.jwt.token";

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setTypeUser(TypeUser.USUARIO_COMUM);

        AuthenticationDTO dto = new AuthenticationDTO(email, senha);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenService.generateToken(user)).thenReturn(fakeToken);

        String result = loginService.login(dto);

        assertEquals(fakeToken, result);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService).generateToken(user);
    }
}