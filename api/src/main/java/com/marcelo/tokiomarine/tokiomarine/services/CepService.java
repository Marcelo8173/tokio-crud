package com.marcelo.tokiomarine.tokiomarine.services;

import com.marcelo.tokiomarine.tokiomarine.DTOs.CepResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CepService {
    private final WebClient webClient;

    public CepService(@Value("${external.service.viacep.baseUrl}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<CepResponseDTO> consultCep(String cep) {
        return webClient.get()
                .uri("{cep}/json/", cep)
                .retrieve()
                .bodyToMono(CepResponseDTO.class) 
                .onErrorResume(e -> Mono.empty()); 
    }
}
