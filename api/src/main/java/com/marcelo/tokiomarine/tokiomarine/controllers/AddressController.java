package com.marcelo.tokiomarine.tokiomarine.controllers;

import com.marcelo.tokiomarine.tokiomarine.DTOs.AddressDTO;
import com.marcelo.tokiomarine.tokiomarine.DTOs.ListDefaultResponseDTO;
import com.marcelo.tokiomarine.tokiomarine.domain.Address;
import com.marcelo.tokiomarine.tokiomarine.domain.exceptions.NotFound;
import com.marcelo.tokiomarine.tokiomarine.domain.exceptions.NotValid;
import com.marcelo.tokiomarine.tokiomarine.services.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("{id}")
    public ResponseEntity<ListDefaultResponseDTO> listAddressByid(@PathVariable UUID id,
                                                                  @RequestParam int page,
                                                                  @RequestParam int size,
                                                                  @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                  @RequestParam(defaultValue = "asc") String direction) {
        ListDefaultResponseDTO address = this.addressService.listAddressById(id, page, size, sortBy, direction);
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or @AddressSecurityPolice.isOwner(#id, authentication)")
    @PutMapping("{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable UUID id, @RequestBody AddressDTO dto) throws NotFound, NotValid {
        Address address = this.addressService.updateAddress(id, dto);
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or @AddressSecurityPolice.isOwner(#id, authentication)")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable UUID id) throws NotFound {
        this.addressService.deleteAddress(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("{id}")
    public ResponseEntity<Void> createNewAddress(@RequestBody @Valid AddressDTO dto,
                                                 @PathVariable UUID id) throws NotFound, NotValid {
        this.addressService.createNewAddress(dto, id);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}


