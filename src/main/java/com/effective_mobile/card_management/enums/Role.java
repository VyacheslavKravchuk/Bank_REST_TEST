package com.effective_mobile.card_management.enums;


import org.springframework.security.core.GrantedAuthority;

/**
 * Перечисление Role (тип пользователя)
 */
public enum Role implements GrantedAuthority {
    ROLE_USER,
    ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}