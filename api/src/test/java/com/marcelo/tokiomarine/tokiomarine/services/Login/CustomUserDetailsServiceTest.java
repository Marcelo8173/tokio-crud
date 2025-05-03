package com.marcelo.tokiomarine.tokiomarine.services.Login;

import com.marcelo.tokiomarine.tokiomarine.domain.User;
import com.marcelo.tokiomarine.tokiomarine.domain.enums.TypeUser;
import com.marcelo.tokiomarine.tokiomarine.domain.login.CustomUserDetails;
import com.marcelo.tokiomarine.tokiomarine.repositories.UserRepository;
import com.marcelo.tokiomarine.tokiomarine.services.Login.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {
    private UserRepository userRepository;
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    @DisplayName(value = "It should be able load user by user_name")
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        String email = "test@example.com";
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setSenha("encodedPass123");
        user.setTypeUser(TypeUser.USUARIO_COMUM);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName(value = "It not should be able load user when is not found")
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(email);
        });

        verify(userRepository).findByEmail(email);
    }
}