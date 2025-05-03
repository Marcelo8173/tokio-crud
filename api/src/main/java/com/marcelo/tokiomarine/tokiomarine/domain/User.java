package com.marcelo.tokiomarine.tokiomarine.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marcelo.tokiomarine.tokiomarine.DTOs.UserDTO;
import com.marcelo.tokiomarine.tokiomarine.domain.enums.TypeUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_user")
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    private String nome;
    private String senha;
    @Enumerated(EnumType.STRING)
    private TypeUser typeUser;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference
    private List<Address> enderecos;

    public User() {}

    public User (UserDTO dto) {
        this.email = dto.email();
        this.nome = dto.nome();
        this.senha = dto.senha();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setTypeUser(TypeUser typeUser) {
        this.typeUser = typeUser;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @JsonIgnore
    public String getPassword() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    @JsonIgnore
    public TypeUser getTypeUser() {
        return typeUser;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
