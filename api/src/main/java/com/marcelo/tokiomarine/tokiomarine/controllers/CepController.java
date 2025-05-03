package com.marcelo.tokiomarine.tokiomarine.controllers;

import com.marcelo.tokiomarine.tokiomarine.DTOs.CepResponseDTO;
import com.marcelo.tokiomarine.tokiomarine.services.CepService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/cep")
public class CepController {

    private final CepService cepService;

    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    @GetMapping("{cep}")
    public Mono<ResponseEntity<CepResponseDTO>> getCepInfo(@PathVariable String cep) {
        return cepService.consultCep(cep)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()));
    }
}
