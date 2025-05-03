package com.marcelo.tokiomarine.tokiomarine.strategy;

import com.marcelo.tokiomarine.tokiomarine.domain.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class RegularUserFilter implements UserFilterStrategy{
    private final UUID userId;
    public RegularUserFilter(UUID userId) {
        this.userId = userId;
    }

    @Override
    public Specification<User> buildSpecification(String search) {
        Specification<User> spec = (root, query, cb) -> cb.equal(root.get("id"), userId);
        if (search != null && !search.isBlank()) {
            String like = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("nome")), like)
            );
        }
        return spec;
    }
}
