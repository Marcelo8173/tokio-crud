package com.marcelo.tokiomarine.tokiomarine.DTOs;

public record AddressDTO(String logradouro,
                         String numero,
                         String complemento,
                         String bairro,
                         String cidade,
                         String estado,
                         String cep) {
}
