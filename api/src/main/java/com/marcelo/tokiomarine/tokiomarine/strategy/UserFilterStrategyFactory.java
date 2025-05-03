package com.marcelo.tokiomarine.tokiomarine.strategy;

import com.marcelo.tokiomarine.tokiomarine.domain.User;
import com.marcelo.tokiomarine.tokiomarine.domain.enums.TypeUser;

public class UserFilterStrategyFactory {
    public static UserFilterStrategy getStrategy(User user) {
        if (user.getTypeUser() == TypeUser.ADMIN) {
            return new AdminUserFilter();
        }
        return new RegularUserFilter(user.getId());
    }
}
