package com.FindersKeepers.backend.service;


import com.FindersKeepers.backend.model.auth.Authorities;

public interface AuthoritiesService {

    Authorities findByName(String name);
}
