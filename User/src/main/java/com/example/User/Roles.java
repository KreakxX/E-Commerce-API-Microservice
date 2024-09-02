package com.example.User;

import org.springframework.security.core.GrantedAuthority;

public enum Roles implements GrantedAuthority {         // Roles Enum
    ADMIN,
    USER;

    @Override
    public String getAuthority() {          // to add Prefix bc in security config when using .hasRole they look for ROLE_ prefix
        return "ROLE_" + name();
    }
}
