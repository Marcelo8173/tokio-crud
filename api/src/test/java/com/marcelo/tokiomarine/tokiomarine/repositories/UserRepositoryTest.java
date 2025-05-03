package com.marcelo.tokiomarine.tokiomarine.repositories;

import com.marcelo.tokiomarine.tokiomarine.domain.User;
import com.marcelo.tokiomarine.tokiomarine.domain.enums.TypeUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class UserRepositoryTest {
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
    }

    @Test
    void shouldFindUserByEmail() {
        String email = "user@example.com";
        UUID id = UUID.randomUUID();

        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setNome("Usuário Teste");
        user.setSenha("senha123");
        user.setTypeUser(TypeUser.USUARIO_COMUM);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> found = userRepository.findByEmail(email);

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(email);
        assertThat(found.get().getId()).isEqualTo(id);
        assertThat(found.get().getNome()).isEqualTo("Usuário Teste");

        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldFindAllUsersBySpecification() {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<User> spec = (root, query, cb) -> cb.conjunction(); // condição sempre verdadeira

        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setEmail("u1@example.com");
        user1.setNome("User One");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setEmail("u2@example.com");
        user2.setNome("User Two");

        List<User> users = List.of(user1, user2);
        Page<User> page = new PageImpl<>(users);

        when(userRepository.findAll(spec, pageable)).thenReturn(page);

        Page<User> result = userRepository.findAll(spec, pageable);

        assertThat(result).hasSize(2);
        assertThat(result.getContent()).extracting(User::getEmail)
                .containsExactly("u1@example.com", "u2@example.com");

        verify(userRepository).findAll(spec, pageable);
    }
}