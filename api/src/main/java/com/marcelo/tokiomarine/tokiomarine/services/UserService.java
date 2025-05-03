package com.marcelo.tokiomarine.tokiomarine.services;

import com.marcelo.tokiomarine.tokiomarine.DTOs.ListDefaultResponseDTO;
import com.marcelo.tokiomarine.tokiomarine.DTOs.UserDTO;
import com.marcelo.tokiomarine.tokiomarine.domain.User;
import com.marcelo.tokiomarine.tokiomarine.domain.enums.TypeUser;
import com.marcelo.tokiomarine.tokiomarine.domain.exceptions.AlredyExist;
import com.marcelo.tokiomarine.tokiomarine.domain.exceptions.NotFound;
import com.marcelo.tokiomarine.tokiomarine.domain.exceptions.NotValid;
import com.marcelo.tokiomarine.tokiomarine.repositories.UserRepository;
import com.marcelo.tokiomarine.tokiomarine.services.Login.TokenService;
import com.marcelo.tokiomarine.tokiomarine.strategy.UserFilterStrategy;
import com.marcelo.tokiomarine.tokiomarine.strategy.UserFilterStrategyFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public void createUser(UserDTO dto) throws AlredyExist {
        if(!isValidPassword(dto.senha())) {
            throw new NotValid(HttpStatus.BAD_REQUEST, "Password not valid");
        }

        Optional<User> hasEmail = this.userRepository.findByEmail(dto.email());

        if (hasEmail.isPresent()) {
            throw new AlredyExist(HttpStatus.BAD_REQUEST, "Email alredy exist");
        }

        User newUser = new User(dto);
        newUser.setTypeUser(TypeUser.USUARIO_COMUM);
        newUser.setSenha(passwordEncoder.encode(dto.senha()));

        this.userRepository.save(newUser);
    }

    public ListDefaultResponseDTO listUsers (int page, int size, String sortBy, String direction, String search, String authToken) throws NotFound {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        UUID userId = tokenService.extractUserId(authToken);
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFound(HttpStatus.BAD_REQUEST, "User not found"));

        UserFilterStrategy strategy = UserFilterStrategyFactory.getStrategy(currentUser);
        Specification<User> spec = strategy.buildSpecification(search);
        Page<User> users = userRepository.findAll(spec, pageable);

        return new ListDefaultResponseDTO(users.getContent(), users.getSize(), users.getNumber());
    }

    private static Boolean isValidPassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\-+_]).{8,}$";
        return Pattern.compile(regex).matcher(password).matches();
    }
}
