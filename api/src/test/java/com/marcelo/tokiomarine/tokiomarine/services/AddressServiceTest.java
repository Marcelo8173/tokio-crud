package com.marcelo.tokiomarine.tokiomarine.services;

import com.marcelo.tokiomarine.tokiomarine.DTOs.AddressDTO;
import com.marcelo.tokiomarine.tokiomarine.DTOs.ListDefaultResponseDTO;
import com.marcelo.tokiomarine.tokiomarine.domain.Address;
import com.marcelo.tokiomarine.tokiomarine.domain.User;
import com.marcelo.tokiomarine.tokiomarine.domain.exceptions.NotFound;
import com.marcelo.tokiomarine.tokiomarine.repositories.AddressRepository;
import com.marcelo.tokiomarine.tokiomarine.repositories.UserRepository;
import com.marcelo.tokiomarine.tokiomarine.services.Login.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class AddressServiceTest {
    private AddressRepository addressRepository;
    private TokenService tokenService;
    private UserRepository userRepository;
    private AddressService addressService;

    @BeforeEach
    void setUp() {
        addressRepository = mock(AddressRepository.class);
        tokenService = mock(TokenService.class);
        userRepository = mock(UserRepository.class);
        addressService = new AddressService(addressRepository, tokenService, userRepository);
    }

    @Test
    @DisplayName(value = "It should be able list Address by id")
    void listAddressById_ShouldReturnList() {
        UUID userId = UUID.randomUUID();
        List<Address> addressList = List.of(new Address());

        Page<Address> page = new PageImpl<>(addressList); // Cria um Page com os dados

        int pageNum = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(pageNum, size);

        when(addressRepository.findByUsuario_Id(eq(userId), any(Pageable.class))).thenReturn(page);

        String sortBy = "createdAt";
        String direction = "asc";

        ListDefaultResponseDTO result = addressService.listAddressById(userId, pageNum, size, sortBy, direction);

        assertEquals(addressList, result.content()); // ou result.getContent() dependendo da sua DTO
        verify(addressRepository).findByUsuario_Id(eq(userId), any(Pageable.class));
    }

    @Test
    @DisplayName(value = "It should be able update address")
    void updateAddress_ShouldUpdateAndReturnAddress() throws NotFound {
        UUID id = UUID.randomUUID();
        AddressDTO dto = new AddressDTO("Rua X", "123", "apto", "Centro", "SP", "SP", "00000000");
        Address address = new Address();
        when(addressRepository.findById(id)).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenAnswer(i -> i.getArgument(0));

        Address updated = addressService.updateAddress(id, dto);

        assertEquals("Rua X", updated.getLogradouro());
        verify(addressRepository).save(address);
    }

    @Test
    @DisplayName(value = "It not should be able update address when id is not found")
    void updateAddress_ShouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();
        AddressDTO dto = new AddressDTO("Rua Y", "456", "", "", "", "", "");

        when(addressRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFound.class, () -> addressService.updateAddress(id, dto));
    }

    @Test
    @DisplayName(value = "It should be able delete an address")
    void deleteAddress_ShouldDeleteIfExists() throws NotFound {
        UUID id = UUID.randomUUID();
        Address address = new Address();

        when(addressRepository.findById(id)).thenReturn(Optional.of(address));

        addressService.deleteAddress(id);

        verify(addressRepository).findById(id);
        verify(addressRepository).deleteById(id);
    }

    @Test
    @DisplayName(value = "It not should be able delete an address when id is not found")
    void deleteAddress_ShouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();

        when(addressRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFound.class, () -> addressService.deleteAddress(id));
    }

    @Test
    @DisplayName(value = "It should be able create a new Address")
    void createNewAddress_ShouldSaveNewAddress() throws NotFound {
        UUID userId = UUID.randomUUID();
        AddressDTO dto = new AddressDTO("Rua Teste", "12", "", "", "", "", "11111111");
        User user = new User();
        String token = "Bearer xyz";

        when(tokenService.extractUserId(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        addressService.createNewAddress(dto, token);

        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository).save(addressCaptor.capture());
        assertEquals("Rua Teste", addressCaptor.getValue().getLogradouro());
    }

    @Test
    @DisplayName(value = "It not should be able create a new Address when user is not found")
    void createNewAddress_ShouldThrowIfUserNotFound() {
        UUID userId = UUID.randomUUID();
        AddressDTO dto = new AddressDTO("Rua Falha", "0", "", "", "", "", "22222222");
        String token = "Bearer token";

        when(tokenService.extractUserId(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFound.class, () -> addressService.createNewAddress(dto, token));
    }

}