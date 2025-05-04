package com.marcelo.tokiomarine.tokiomarine.services;

import com.marcelo.tokiomarine.tokiomarine.DTOs.UserDTO;
import com.marcelo.tokiomarine.tokiomarine.DTOs.UserEditDTO;
import com.marcelo.tokiomarine.tokiomarine.domain.User;
import com.marcelo.tokiomarine.tokiomarine.domain.enums.TypeUser;
import com.marcelo.tokiomarine.tokiomarine.domain.exceptions.AlredyExist;
import com.marcelo.tokiomarine.tokiomarine.domain.exceptions.NotFound;
import com.marcelo.tokiomarine.tokiomarine.domain.exceptions.NotValid;
import com.marcelo.tokiomarine.tokiomarine.repositories.UserRepository;
import com.marcelo.tokiomarine.tokiomarine.services.Login.TokenService;
import com.marcelo.tokiomarine.tokiomarine.strategy.UserFilterStrategy;
import com.marcelo.tokiomarine.tokiomarine.strategy.UserFilterStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName(value = "It should be able create new user")
    void createUser_ShouldSaveUser_WhenValidData() throws AlredyExist {
        UserDTO dto = new UserDTO("test@example.com", "Test User", "Senha123!");
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.senha())).thenReturn("hashedPassword");

        userService.createUser(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(TypeUser.USUARIO_COMUM, savedUser.getTypeUser());
        assertEquals("hashedPassword", savedUser.getPassword());
        assertEquals("test@example.com", savedUser.getEmail());
    }

    @Test
    @DisplayName(value = "It not should be able create new user when email alredy exist")
    void createUser_ShouldThrowAlredyExist_WhenEmailAlreadyExists() {
        UserDTO dto = new UserDTO("Test User", "test@example.com", "Senha123!");
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(new User()));

        assertThrows(AlredyExist.class, () -> userService.createUser(dto));
    }

    @Test
    @DisplayName(value = "It not should be able create new user when email password is not valid")
    void createUser_ShouldThrowNotValid_WhenPasswordInvalid() {
        UserDTO dto = new UserDTO("Test User", "test@example.com", "senha");

        assertThrows(NotValid.class, () -> userService.createUser(dto));
    }

    @Test
    @DisplayName(value = "It should be able list users")
    void listUsers_ShouldReturnList_WhenValidRequest() throws NotFound {
        UUID userId = UUID.randomUUID();
        String token = "Bearer token";
        User currentUser = new User();
        currentUser.setId(userId);

        when(tokenService.extractUserId(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));

        UserFilterStrategy strategy = mock(UserFilterStrategy.class);
        Specification<User> spec = (root, query, cb) -> null;

        when(strategy.buildSpecification("search")).thenReturn(spec);
        mockStatic(UserFilterStrategyFactory.class).when(() -> UserFilterStrategyFactory.getStrategy(currentUser)).thenReturn(strategy);

        List<User> userList = List.of(new User(), new User());
        Page<User> userPage = new PageImpl<>(userList, PageRequest.of(0, 10), userList.size());

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(userPage);

        var result = userService.listUsers(0, 10, "id", "ASC", "search", token);

        assertEquals(2, result.content().size());
        assertEquals(10, result.pageSize());
        assertEquals(0, result.pageNumber());
    }

    @Test
    @DisplayName(value = "It not should be able list users when user not found")
    void listUsers_ShouldThrowNotFound_WhenUserNotExists() {
        UUID userId = UUID.randomUUID();
        String token = "Bearer token";

        when(tokenService.extractUserId(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFound.class, () -> userService.listUsers(0, 10, "id", "ASC", "", token));
    }

    @Test
    @DisplayName("It should update user when email is not in use")
    void updateUser_ShouldUpdateUser_WhenEmailNotExists() throws AlredyExist, NotFound {
        UUID userId = UUID.randomUUID();
        UserEditDTO editDTO = new UserEditDTO("Novo Nome", "newemail@mail.com");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("oldemail@mail.com");
        existingUser.setNome("Nome Antigo");

        when(userRepository.findByEmail(editDTO.email())).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        userService.updateUser(editDTO, userId);

        assertEquals("newemail@mail.com", existingUser.getEmail());
        assertEquals("Novo Nome", existingUser.getNome());
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("It should throw AlredyExist when email already exists")
    void updateUser_ShouldThrowAlredyExist_WhenEmailExists() {
        UUID userId = UUID.randomUUID();
        UserEditDTO editDTO = new UserEditDTO("existing@mail.com", "Novo Nome");

        when(userRepository.findByEmail(editDTO.email())).thenReturn(Optional.of(new User()));

        assertThrows(AlredyExist.class, () -> userService.updateUser(editDTO, userId));
    }


}