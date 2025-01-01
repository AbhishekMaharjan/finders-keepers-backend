package com.FindersKeepers.backend.enums;

import lombok.Getter;

@Getter
public enum RoleTypes {
    SUPER_ADMIN("SUPER ADMIN"),
    ADMIN("ADMIN"),
    USERS("USERS");
    private final String value;

    RoleTypes(String value) {
        this.value = value;
    }
    }
