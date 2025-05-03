package com.marcelo.tokiomarine.tokiomarine.repositories;

import com.marcelo.tokiomarine.tokiomarine.domain.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepository  extends JpaRepository<Address, UUID> {
    Page<Address> findByUsuario_Id(UUID id, Pageable pageable);
}
