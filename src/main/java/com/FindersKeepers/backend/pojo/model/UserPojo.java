package com.FindersKeepers.backend.pojo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPojo {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private AddressPojo addressPojo;
}
