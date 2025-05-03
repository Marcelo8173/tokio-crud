package com.marcelo.tokiomarine.tokiomarine.repositories;

import com.marcelo.tokiomarine.tokiomarine.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
//    @EntityGraph(attributePaths = "enderecos")
    Page<User> findAll(Specification<User> spec, Pageable pageable);
}
