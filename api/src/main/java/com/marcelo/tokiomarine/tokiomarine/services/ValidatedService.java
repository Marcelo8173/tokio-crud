package com.marcelo.tokiomarine.tokiomarine.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ValidatedService {
    private RestTemplate restTemplate;
    @Value("${external.service.viacep.baseUrl}")
    private String url;

    public ValidatedService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public boolean validationCep(String cep) {
        String finalUrl = this.url + cep + "/json/";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    finalUrl,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Object erro = body.get("erro");
                return erro == null;
            }

        } catch (Exception e) {
            return false;
        }

        return false;
    }

}
