package com.marcelo.tokiomarine.tokiomarine.services;

import com.marcelo.tokiomarine.tokiomarine.DTOs.AddressDTO;
import com.marcelo.tokiomarine.tokiomarine.DTOs.ListDefaultResponseDTO;
import com.marcelo.tokiomarine.tokiomarine.domain.Address;
import com.marcelo.tokiomarine.tokiomarine.domain.User;
import com.marcelo.tokiomarine.tokiomarine.domain.exceptions.NotFound;
import com.marcelo.tokiomarine.tokiomarine.domain.exceptions.NotValid;
import com.marcelo.tokiomarine.tokiomarine.repositories.AddressRepository;
import com.marcelo.tokiomarine.tokiomarine.repositories.UserRepository;
import com.marcelo.tokiomarine.tokiomarine.services.Login.TokenService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final ValidatedService validatedService;


    public AddressService(AddressRepository addressRepository,
                          TokenService tokenService,
                          UserRepository userRepository,
                          ValidatedService validatedService) {
        this.addressRepository = addressRepository;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.validatedService = validatedService;
    }

    public ListDefaultResponseDTO listAddressById(UUID id, int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Address> pageResult = this.addressRepository.findByUsuario_Id(id, pageRequest);
        return new ListDefaultResponseDTO(pageResult.getContent(), pageResult.getSize(), pageResult.getNumber());
    }

    public Address updateAddress(UUID id, AddressDTO dto) throws NotFound, NotValid {
        Address address = this.addressRepository.findById(id)
                .orElseThrow(() -> new NotFound(HttpStatus.BAD_REQUEST, "Address not found"));

        boolean isValid = this.validatedService.validationCep(dto.cep());
        if(!isValid) {
            throw new NotValid(HttpStatus.BAD_REQUEST, "this cep is not valid");
        }
        address.setLogradouro(dto.logradouro());
        address.setNumero(dto.numero());
        address.setComplemento(dto.complemento());
        address.setBairro(dto.bairro());
        address.setCidade(dto.cidade());
        address.setEstado(dto.estado());
        address.setCep(dto.cep());
        return this.addressRepository.save(address);
    }

    public void deleteAddress(UUID id) throws NotFound {
        this.addressRepository.findById(id).orElseThrow(() -> new NotFound(HttpStatus.BAD_REQUEST, "Address not found"));
        this.addressRepository.deleteById(id);
    }

    public void createNewAddress(AddressDTO dto, String auth) throws NotFound, NotValid{
        UUID userId = tokenService.extractUserId(auth);
        Optional<User> user = this.userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFound(HttpStatus.BAD_REQUEST, "User nor found");
        }

        boolean isValid = this.validatedService.validationCep(dto.cep());
        if(!isValid) {
            throw new NotValid(HttpStatus.BAD_REQUEST, "this cep is not valid");
        }
        Address newAddress = new Address(dto, user.orElseThrow());
        this.addressRepository.save(newAddress);
    }
}
