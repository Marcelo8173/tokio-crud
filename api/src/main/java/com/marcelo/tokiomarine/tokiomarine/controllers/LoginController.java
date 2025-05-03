package com.marcelo.tokiomarine.tokiomarine.controllers;

import com.marcelo.tokiomarine.tokiomarine.DTOs.AuthenticationDTO;
import com.marcelo.tokiomarine.tokiomarine.domain.login.DTOs.LoginResponseDTO;
import com.marcelo.tokiomarine.tokiomarine.services.Login.LoginService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO dto) {
        try {
            String token = this.loginService.login(dto);
            return new ResponseEntity<>(new LoginResponseDTO(token), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}