package com.FindersKeepers.backend.config.security.utils;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record CustomPasswordUser(String username, Collection<GrantedAuthority> authorities) {

}