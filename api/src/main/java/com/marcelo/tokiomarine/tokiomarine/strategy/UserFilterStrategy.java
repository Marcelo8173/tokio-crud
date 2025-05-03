package com.marcelo.tokiomarine.tokiomarine.strategy;

import com.marcelo.tokiomarine.tokiomarine.domain.User;
import org.springframework.data.jpa.domain.Specification;

public interface UserFilterStrategy {
    Specification<User> buildSpecification(String search);
}
