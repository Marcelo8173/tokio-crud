package com.marcelo.tokiomarine.tokiomarine.repositories;


import com.marcelo.tokiomarine.tokiomarine.domain.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AddressRepositoryTest {
    @Test
    @DisplayName("Should call findByUsuario_Id and return a Page of Address")
    void findByUsuarioId_ShouldReturnPage() {
        AddressRepository addressRepository = mock(AddressRepository.class);
        UUID userId = UUID.randomUUID();
        Address address = new Address();
        Page<Address> page = new PageImpl<>(List.of(address));

        Pageable pageable = PageRequest.of(0, 10);
        when(addressRepository.findByUsuario_Id(userId, pageable)).thenReturn(page);
        Page<Address> result = addressRepository.findByUsuario_Id(userId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(address, result.getContent().get(0));
        verify(addressRepository, times(1)).findByUsuario_Id(userId, pageable);
    }
}