package com.marcelo.tokiomarine.tokiomarine.database;

import com.marcelo.tokiomarine.tokiomarine.DTOs.UserDTO;
import com.marcelo.tokiomarine.tokiomarine.domain.User;
import com.marcelo.tokiomarine.tokiomarine.domain.enums.TypeUser;
import com.marcelo.tokiomarine.tokiomarine.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Seeds implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public Seeds(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) throws Exception {
        UserDTO userDTO = new UserDTO("admin@mail.com", "admin", "Admin@123");

        Optional<User> user = userRepository.findByEmail(userDTO.email());
        if (user.isEmpty()) {
            User userAdm = new User(userDTO);
            userAdm.setSenha(encoder.encode(userDTO.senha()));
            userAdm.setTypeUser(TypeUser.ADMIN);
            userRepository.save(userAdm);
        }
    }
}
