package com.malibu.app.dto;

import lombok.Value;

@Value
public class JwtAuthenticationResponse {
    private String accessToken;
    private UserInfo user;
}
