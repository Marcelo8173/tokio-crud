package com.marcelo.tokiomarine.tokiomarine.domain.security;

import com.marcelo.tokiomarine.tokiomarine.domain.login.CustomUserDetails;
import com.marcelo.tokiomarine.tokiomarine.repositories.AddressRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("AddressSecurityPolice")
public class AddressSecurityPolice {
    private final AddressRepository addressRepository;

    public AddressSecurityPolice (AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public boolean isOwner(UUID addressId, Authentication auth) {
        UUID userId = ((CustomUserDetails) auth.getPrincipal()).getId();
        return addressRepository.findById(addressId)
                .map(address -> address.getUsuario().getId().equals(userId))
                .orElse(true);
    }
}
