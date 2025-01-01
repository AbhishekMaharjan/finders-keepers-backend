package com.FindersKeepers.backend.config.security.user;


import com.FindersKeepers.backend.config.resources.CustomMessageSource;
import com.FindersKeepers.backend.model.Users;
import com.FindersKeepers.backend.repository.UserRepository;
import com.FindersKeepers.backend.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.FindersKeepers.backend.constant.message.ErrorConstantValue.ERROR_EMAIL_NOT_FOUND;
import static com.FindersKeepers.backend.constant.message.ErrorConstantValue.ERROR_INVALID_CREDENTIAL;
import static com.FindersKeepers.backend.constant.message.FieldConstantValue.EMAIL;


@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CustomMessageSource customMessageSource;
    private final AuthorizationService authorizationService;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> optionalUsers = userRepository.findByEmail(username);
        if (optionalUsers.isEmpty())
            throw new UsernameNotFoundException(customMessageSource.get(ERROR_EMAIL_NOT_FOUND, customMessageSource.get(EMAIL)));
        Users user = optionalUsers.get();
        if (!user.getEmail().equals(username)) {
            throw new UsernameNotFoundException(customMessageSource.get(ERROR_INVALID_CREDENTIAL));
        }
        List<String> authorities = new ArrayList<>();
        user.getAuthorities().forEach(auth -> authorities.add(auth.getAuthority()));
        Collection<GrantedAuthority> authorizes = new HashSet<>();
        user.getAuthorities().forEach(auth -> authorizes.add(new SimpleGrantedAuthority(auth.getAuthority())));
        authorizes.add(new SimpleGrantedAuthority("OAUTH_USER"));
        userRepository.save(user);
        return new User(user.getEmail(), user.getPassword(), user.getIsEnabled(), true,
                true, true, authorizes);
    }

}