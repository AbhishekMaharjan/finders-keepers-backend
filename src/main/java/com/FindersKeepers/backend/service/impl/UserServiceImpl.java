package com.FindersKeepers.backend.service.impl;


import com.FindersKeepers.backend.config.resources.CustomMessageSource;
import com.FindersKeepers.backend.enums.RoleTypes;
import com.FindersKeepers.backend.exception.NotFoundException;
import com.FindersKeepers.backend.model.Users;
import com.FindersKeepers.backend.model.auth.Authorities;
import com.FindersKeepers.backend.pojo.model.UserPojo;
import com.FindersKeepers.backend.records.UsersUpdateRecord;
import com.FindersKeepers.backend.repository.UserRepository;
import com.FindersKeepers.backend.service.AddressService;
import com.FindersKeepers.backend.service.AuthoritiesService;
import com.FindersKeepers.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.FindersKeepers.backend.constant.message.ErrorConstantValue.ERROR_ID_NOT_FOUND;
import static com.FindersKeepers.backend.constant.message.FieldConstantValue.USER;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomMessageSource customMessageSource;
    private final AuthoritiesService authoritiesService;
    private final AddressService addressService;

    @Override
    public Users findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(customMessageSource.get(ERROR_ID_NOT_FOUND, customMessageSource.get(USER))));
    }

    @Override
    @Transactional
    public Users save(UserPojo userPojo) {
        Users users = new Users();
        users.setEmail(userPojo.getEmail());
        users.setFirstName(userPojo.getFirstName());
        users.setLastName(userPojo.getLastName());
        users.setPhoneNumber(userPojo.getPhoneNumber());
        Set<Authorities> authoritiesSet = new HashSet<>();
        Authorities authorities = authoritiesService.findByName(RoleTypes.USERS.name());
        authoritiesSet.add(authorities);
        users.setAuthorities(authoritiesSet);
        if (Objects.nonNull(userPojo.getPassword())) {
            users.setPassword(passwordEncoder.encode(userPojo.getPassword()));
            users.setLastPasswordChangeDate(new Date());
        }
        users.setAddress(addressService.save(userPojo.getAddressPojo(), null));
        return userRepository.save(users);
    }

    @Override
    public Users update(UsersUpdateRecord usersUpdateRecord, Long userId) {
        Users users = findById(userId);
        users.setPhoneNumber(usersUpdateRecord.phoneNumber());
        users.setFirstName(usersUpdateRecord.firstName());
        users.setLastName(usersUpdateRecord.lastName());
        return userRepository.save(users);
    }

    @Override
    public List<Users> getAllUsers() {
        return userRepository.findAllByIdAfter(1L);
    }
}
