package com.marcelo.tokiomarine.tokiomarine.DTOs;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
public record UserDTO(
        @NotBlank(message = "email is required")
        String email,
        @NotBlank(message = "nome is required")
        String nome,
        @NotBlank(message = "The field 'senha' is required")
        @Size(min = 8, message = "The field 'senha' need to 8 char")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$", message = "The 'senha' is not strong")
        String senha) {
}
