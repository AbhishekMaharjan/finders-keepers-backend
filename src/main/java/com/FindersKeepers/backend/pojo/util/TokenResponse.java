package com.FindersKeepers.backend.pojo.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TokenResponse implements Serializable {
    private String refresh_token;
    private String access_token;
    private Long expires_in;
}
