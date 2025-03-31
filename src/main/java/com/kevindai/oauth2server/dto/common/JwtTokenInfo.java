package com.kevindai.oauth2server.dto.common;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JwtTokenInfo {
    private String token;
    private long expiresIn;
    private String jti;
}
