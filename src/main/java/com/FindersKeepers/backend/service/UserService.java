package com.FindersKeepers.backend.service;


import com.FindersKeepers.backend.model.Users;
import com.FindersKeepers.backend.pojo.model.UserPojo;
import com.FindersKeepers.backend.records.UsersUpdateRecord;

import java.util.List;

public interface UserService {

    Users findById(Long id);

    Users save(UserPojo userPojo);

    Users update(UsersUpdateRecord usersUpdateRecord, Long userId);

    List<Users> getAllUsers();
}
