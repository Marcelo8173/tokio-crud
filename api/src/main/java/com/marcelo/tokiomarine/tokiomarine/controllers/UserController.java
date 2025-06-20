package com.marcelo.tokiomarine.tokiomarine.controllers;

import com.marcelo.tokiomarine.tokiomarine.DTOs.ListDefaultResponseDTO;
import com.marcelo.tokiomarine.tokiomarine.DTOs.UserDTO;
import com.marcelo.tokiomarine.tokiomarine.DTOs.UserEditDTO;
import com.marcelo.tokiomarine.tokiomarine.domain.exceptions.AlredyExist;
import com.marcelo.tokiomarine.tokiomarine.domain.exceptions.NotFound;
import com.marcelo.tokiomarine.tokiomarine.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Void> createNewUser(@RequestBody @Valid UserDTO dto) throws AlredyExist {
        this.userService.createUser(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ListDefaultResponseDTO> listUsers(@RequestParam int page,
                                                            @RequestParam int size,
                                                            @RequestParam(defaultValue = "nome") String sortBy,
                                                            @RequestParam(defaultValue = "asc") String direction,
                                                            @RequestParam(defaultValue = "") String search,
                                                            @RequestHeader(name = "Authorization") String authorizationHeader) throws NotFound {
        ListDefaultResponseDTO listUsers = this.userService.listUsers(page, size, sortBy, direction, search, authorizationHeader);
        return new ResponseEntity<>(listUsers, HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> updateUser(@RequestBody @Valid UserEditDTO dto, @PathVariable UUID id) throws AlredyExist, NotFound {
        this.userService.updateUser(dto, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) throws NotFound {
        this.userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
