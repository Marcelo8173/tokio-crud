package com.marcelo.tokiomarine.tokiomarine.strategy;

import com.marcelo.tokiomarine.tokiomarine.domain.User;
import org.springframework.data.jpa.domain.Specification;

public class AdminUserFilter implements UserFilterStrategy{
    @Override
    public Specification<User> buildSpecification(String search) {
        Specification<User> spec = Specification.where(null);
        if (search != null && !search.isBlank()) {
            String like = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("nome")), like),
                    cb.like(cb.lower(root.get("email")), like),
                    cb.like(cb.lower(root.get("createdAt")), like)
            ));
        }
        return spec;
    }
}
