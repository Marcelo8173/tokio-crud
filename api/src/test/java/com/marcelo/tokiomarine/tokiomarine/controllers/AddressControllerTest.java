package com.marcelo.tokiomarine.tokiomarine.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcelo.tokiomarine.tokiomarine.DTOs.AddressDTO;
import com.marcelo.tokiomarine.tokiomarine.DTOs.AuthenticationDTO;
import com.marcelo.tokiomarine.tokiomarine.DTOs.UserDTO;
import com.marcelo.tokiomarine.tokiomarine.domain.User;
import com.marcelo.tokiomarine.tokiomarine.domain.enums.TypeUser;
import com.marcelo.tokiomarine.tokiomarine.repositories.UserRepository;
import com.marcelo.tokiomarine.tokiomarine.services.Login.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class AddressControllerTest {
    @InjectMocks
    private AddressController addressController;
    @InjectMocks
    private LoginController loginController;
    @Autowired
    private UserRepository userRepository;
    @InjectMocks
    private UserController userController;

    @InjectMocks
    private TokenService tokenService;
    @Autowired
    TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;

    @Test
    @DisplayName(value = "It should be able create a new user")
    @DirtiesContext
    void createNewUser() throws JsonProcessingException {
        UserDTO userDTO = new UserDTO("user123@mail.com", "admin", "User@123");

        Optional<User> existingUser = userRepository.findByEmail(userDTO.email());
        if (existingUser.isPresent()) {
            userRepository.delete(existingUser.get());
        }
        String createUserUrl = "http://localhost:" + port + "/user";
        ResponseEntity<String> createUser = restTemplate.postForEntity(
                createUserUrl,
                userDTO,
                String.class
        );

        assertEquals(HttpStatus.CREATED, createUser.getStatusCode());

        AuthenticationDTO authenticationDTO = new AuthenticationDTO(userDTO.email(), userDTO.senha());
        String loginUrl = "http://localhost:" + port + "/auth/login";
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                loginUrl,
                authenticationDTO,
                String.class
        );
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        String response = loginResponse.getBody();
        ObjectMapper mapper = new ObjectMapper();
        String token = mapper.readTree(response).get("token").asText();
        AddressDTO addressDTO = new AddressDTO(
                "Rua ABC",
                "123",
                "Apto 101",
                "Centro",
                "São Paulo",
                "SP",
                "03065070"
        );

        UUID userId = tokenService.extractUserId(token);

        String url = "http://localhost:" + port + "/address/" + userId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<AddressDTO> entity = new HttpEntity<>(addressDTO, headers);
        ResponseEntity<Void> result = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    @DisplayName("It should be able to list address by user id with pagination")
    void listAddressById_shouldReturnPaginatedAddressList() throws JsonProcessingException {
        UserDTO userDTO = new UserDTO("user123@mail.com", "admin", "User@123");
        Optional<User> existingUser = userRepository.findByEmail(userDTO.email());
        if (existingUser.isPresent()) {
            userRepository.delete(existingUser.get());
        }
        String createUserUrl = "http://localhost:" + port + "/user";
        restTemplate.postForEntity(createUserUrl, userDTO, String.class);

        AuthenticationDTO authenticationDTO = new AuthenticationDTO(userDTO.email(), userDTO.senha());
        String loginUrl = "http://localhost:" + port + "/auth/login";
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, authenticationDTO, String.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        ObjectMapper mapper = new ObjectMapper();
        String token = mapper.readTree(loginResponse.getBody()).get("token").asText();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<?> authEntity = new HttpEntity<>(headers);

        String listUsersUrl = "http://localhost:" + port + "/user?size=10&page=0&direction=desc&sortBy=createdAt";
        ResponseEntity<String> usersResponse = restTemplate.exchange(
                listUsersUrl,
                HttpMethod.GET,
                authEntity,
                String.class
        );
        assertEquals(HttpStatus.OK, usersResponse.getStatusCode());

        JsonNode usersJson = mapper.readTree(usersResponse.getBody());
        JsonNode contentArray = usersJson.get("content");
        assertTrue(contentArray.isArray());

        UUID userId = null;
        for (JsonNode userNode : contentArray) {
            if (userNode.get("email").asText().equals(userDTO.email())) {
                userId = UUID.fromString(userNode.get("id").asText());
                break;
            }
        }

        assertNotNull(userId, "User ID should not be null");

        AddressDTO addressDTO = new AddressDTO(
                "Rua ABC",
                "123",
                "Apto 101",
                "Centro",
                "São Paulo",
                "SP",
                "03065070"
        );
        HttpEntity<AddressDTO> addressEntity = new HttpEntity<>(addressDTO, headers);
        ResponseEntity<Void> createAddressResponse = restTemplate.exchange(
                "http://localhost:" + port + "/address/" + userId,
                HttpMethod.POST,
                addressEntity,
                Void.class
        );
        assertEquals(HttpStatus.CREATED, createAddressResponse.getStatusCode());

        String url = "http://localhost:" + port + "/address/" + userId + "?page=0&size=10&sortBy=createdAt&direction=asc";
        ResponseEntity<String> getResponse = restTemplate.exchange(
                url,
                HttpMethod.GET,
                authEntity,
                String.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        JsonNode resultJson = mapper.readTree(getResponse.getBody());
        assertTrue(resultJson.has("content"));
        assertTrue(resultJson.get("content").isArray());
        assertFalse(resultJson.get("content").isEmpty());
    }

    @Test
    @DisplayName("It should update address by ID if user is owner or admin")
    void updatedAddress_shouldSucceedForOwnerOrAdmin() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        UserDTO userDTO = new UserDTO("delete@mail.com", "admin", "User@123");
        Optional<User> existingUser = userRepository.findByEmail(userDTO.email());
        if (existingUser.isPresent()) {
            userRepository.delete(existingUser.get());
        }
        restTemplate.postForEntity("http://localhost:" + port + "/user", userDTO, String.class);

        AuthenticationDTO authDTO = new AuthenticationDTO(userDTO.email(), userDTO.senha());
        String token = mapper.readTree(
                restTemplate.postForEntity("http://localhost:" + port + "/auth/login", authDTO, String.class).getBody()
        ).get("token").asText();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> usersResponse = restTemplate.exchange(
                "http://localhost:" + port + "/user?size=10&page=0",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        UUID userId = null;
        for (JsonNode userNode : mapper.readTree(usersResponse.getBody()).get("content")) {
            if (userNode.get("email").asText().equals(userDTO.email())) {
                userId = UUID.fromString(userNode.get("id").asText());
                break;
            }
        }
        assertNotNull(userId);

        AddressDTO addressDTO = new AddressDTO("Rua B", "321", "", "Bairro", "Cidade", "SP", "03065070");
        restTemplate.exchange(
                "http://localhost:" + port + "/address/" + userId,
                HttpMethod.POST,
                new HttpEntity<>(addressDTO, headers),
                String.class
        );

        String listUrl = "http://localhost:" + port + "/address/" + userId + "?page=0&size=10";
        ResponseEntity<String> addressListResponse = restTemplate.exchange(
                listUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        JsonNode addressContent = mapper.readTree(addressListResponse.getBody()).get("content");
        UUID addressId = UUID.fromString(addressContent.get(0).get("id").asText());

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "http://localhost:" + port + "/address/" + addressId,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        ResponseEntity<String> getResponse = restTemplate.exchange(
                "http://localhost:" + port + "/address/" + userId + "?page=0&size=10",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        JsonNode updatedList = mapper.readTree(getResponse.getBody()).get("content");
        assertTrue(updatedList.isArray());
        assertTrue(updatedList.isEmpty() || updatedList.findValues("id").stream().noneMatch(n -> n.asText().equals(addressId.toString())));
    }


    @Test
    @DisplayName("It should delete address by ID if user is owner or admin")
    void deleteAddress_shouldSucceedForOwnerOrAdmin() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        UserDTO userDTO = new UserDTO("delete@mail.com", "admin", "User@123");
        Optional<User> existingUser = userRepository.findByEmail(userDTO.email());
        if (existingUser.isPresent()) {
            userRepository.delete(existingUser.get());
        }
        restTemplate.postForEntity("http://localhost:" + port + "/user", userDTO, String.class);

        AuthenticationDTO authDTO = new AuthenticationDTO(userDTO.email(), userDTO.senha());
        String token = mapper.readTree(
                restTemplate.postForEntity("http://localhost:" + port + "/auth/login", authDTO, String.class).getBody()
        ).get("token").asText();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> usersResponse = restTemplate.exchange(
                "http://localhost:" + port + "/user?size=10&page=0",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        UUID userId = null;
        for (JsonNode userNode : mapper.readTree(usersResponse.getBody()).get("content")) {
            if (userNode.get("email").asText().equals(userDTO.email())) {
                userId = UUID.fromString(userNode.get("id").asText());
                break;
            }
        }
        assertNotNull(userId);

        AddressDTO addressDTO = new AddressDTO("Rua B", "321", "", "Bairro", "Cidade", "SP", "11111-111");
        restTemplate.exchange(
                "http://localhost:" + port + "/address/" + userId,
                HttpMethod.POST,
                new HttpEntity<>(addressDTO, headers),
                String.class
        );

        String listUrl = "http://localhost:" + port + "/address/" + userId + "?page=0&size=10";
        ResponseEntity<String> addressListResponse = restTemplate.exchange(
                listUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        JsonNode addressContent = mapper.readTree(addressListResponse.getBody()).get("content");
        UUID addressId = UUID.fromString(addressContent.get(0).get("id").asText());

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "http://localhost:" + port + "/address/" + addressId,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        ResponseEntity<String> getResponse = restTemplate.exchange(
                "http://localhost:" + port + "/address/" + userId + "?page=0&size=10",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        JsonNode updatedList = mapper.readTree(getResponse.getBody()).get("content");
        assertTrue(updatedList.isArray());
        assertTrue(updatedList.isEmpty() || updatedList.findValues("id").stream().noneMatch(n -> n.asText().equals(addressId.toString())));
    }


}