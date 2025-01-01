package com.FindersKeepers.backend.service;


import com.FindersKeepers.backend.model.Users;

public interface AuthorizationService {

    Users findUsersByPrinciple(String principle);

}
