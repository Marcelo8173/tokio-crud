package com.marcelo.tokiomarine.tokiomarine.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcelo.tokiomarine.tokiomarine.DTOs.AuthenticationDTO;
import com.marcelo.tokiomarine.tokiomarine.DTOs.UserDTO;
import com.marcelo.tokiomarine.tokiomarine.DTOs.UserEditDTO;
import com.marcelo.tokiomarine.tokiomarine.domain.User;
import com.marcelo.tokiomarine.tokiomarine.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("It should create a new user")
    @DirtiesContext
    void createNewUser() throws JsonProcessingException {
        UserDTO userDTO = new UserDTO("user123@mail.com", "admin", "User@123");

        Optional<User> existingUser = userRepository.findByEmail(userDTO.email());
        if (existingUser.isPresent()) {
            userRepository.delete(existingUser.get());
        }

        String createUserUrl = "http://localhost:" + port + "/user";
        ResponseEntity<String> createUserResponse = restTemplate.postForEntity(
                createUserUrl,
                userDTO,
                String.class
        );

        assertEquals(HttpStatus.CREATED, createUserResponse.getStatusCode());
    }

    @Test
    @DisplayName("It should list users")
    void listUsers() throws JsonProcessingException {
        UserDTO userDTO = new UserDTO("user123@mail.com", "admin", "User@123");
        Optional<User> existingUser = userRepository.findByEmail(userDTO.email());
        if (existingUser.isPresent()) {
            userRepository.delete(existingUser.get());
        }
        String createUserUrl = "http://localhost:" + port + "/user";
        restTemplate.postForEntity(createUserUrl, userDTO, String.class);

        String loginUrl = "http://localhost:" + port + "/auth/login";
        AuthenticationDTO loginDTO = new AuthenticationDTO(userDTO.email(), userDTO.senha());
        ObjectMapper mapper = new ObjectMapper();
        String token = mapper.readTree(
                restTemplate.postForEntity(loginUrl, loginDTO, String.class).getBody()
        ).get("token").asText();


        String listUsersUrl = "http://localhost:" + port + "/user?size=10&page=0&direction=desc&sortBy=createdAt";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> listUsersResponse = restTemplate.exchange(
                listUsersUrl,
                HttpMethod.GET,
                entity,
                String.class
        );
        assertEquals(HttpStatus.OK, listUsersResponse.getStatusCode());

        mapper = new ObjectMapper();
        JsonNode usersJson = mapper.readTree(listUsersResponse.getBody());
        JsonNode contentArray = usersJson.get("content");
        assertTrue(contentArray.isArray());
        assertFalse(contentArray.isEmpty());
    }

    @Test
    @DisplayName("It should update an existing user")
    void updateUser() throws JsonProcessingException {
        UserDTO userDTO = new UserDTO("user123@mail.com", "admin", "User@123");

        Optional<User> existingUser = userRepository.findByEmail(userDTO.email());
        if (existingUser.isPresent()) {
            userRepository.delete(existingUser.get());
        }

        String createUserUrl = "http://localhost:" + port + "/user";
        restTemplate.postForEntity(createUserUrl, userDTO, String.class);

        String loginUrl = "http://localhost:" + port + "/auth/login";
        AuthenticationDTO loginDTO = new AuthenticationDTO(userDTO.email(), userDTO.senha());
        ObjectMapper mapper = new ObjectMapper();
        String token = mapper.readTree(
                restTemplate.postForEntity(loginUrl, loginDTO, String.class).getBody()
        ).get("token").asText();

        UserEditDTO userEditDTO = new UserEditDTO("admin", "newuser@mail.com");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        User user = userRepository.findByEmail(userDTO.email()).get();

        String updateUserUrl = "http://localhost:" + port + "/user/" + user.getId();

        HttpEntity<UserEditDTO> entity = new HttpEntity<>(userEditDTO, headers);
        ResponseEntity<Void> updateUserResponse = restTemplate.exchange(
                updateUserUrl,
                HttpMethod.PUT,
                entity,
                Void.class
        );

        assertEquals(HttpStatus.OK, updateUserResponse.getStatusCode());

        User updatedUser = userRepository.findById(user.getId()).get();
        assertEquals(userEditDTO.email(), updatedUser.getEmail());
    }
}
