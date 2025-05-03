package com.marcelo.tokiomarine.tokiomarine.services.Login;


import com.marcelo.tokiomarine.tokiomarine.DTOs.AuthenticationDTO;
import com.marcelo.tokiomarine.tokiomarine.domain.login.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;


@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    public LoginService(TokenService tokenService, AuthenticationManager authenticationManager) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    public String login(AuthenticationDTO authenticationDTO) {
        var userNamePassword = new UsernamePasswordAuthenticationToken(authenticationDTO.email(), authenticationDTO.senha());

        var auth = this.authenticationManager.authenticate(userNamePassword);
        var userDetails = (CustomUserDetails) auth.getPrincipal();
        var user = userDetails.getUserEntity();

        return this.tokenService.generateToken(user);
    }
}
