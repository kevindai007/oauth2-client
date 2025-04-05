package com.kevindai.oauth2server.service;

import com.kevindai.base.projcore.exception.BizException;
import com.kevindai.oauth2server.dto.common.JwtTokenInfo;
import com.kevindai.oauth2server.dto.user.UsersLoginRequestDto;
import com.kevindai.oauth2server.dto.user.UsersLoginResponseDto;
import com.kevindai.oauth2server.entity.RefreshTokenEntity;
import com.kevindai.oauth2server.entity.UsersEntity;
import com.kevindai.oauth2server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UsersService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PermissionService permissionService;

    public UsersLoginResponseDto login(UsersLoginRequestDto usersLoginRequestDto) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usersLoginRequestDto.getUsername(), usersLoginRequestDto.getPassword()));
        if (!authenticate.isAuthenticated()) {
            throw new BizException(HttpStatus.UNAUTHORIZED);
        }
        UsersEntity usersEntity = userRepository.findByUsername(usersLoginRequestDto.getUsername());
        List<String> permissions = permissionService.getPermissionsByUserId(usersEntity.getId());


        JwtTokenInfo tokenInfo = jwtService.generateToken(usersEntity.getId());
        RefreshTokenEntity refreshTokenEntity = jwtService.generateRefreshToken(usersEntity, permissions);
        return UsersLoginResponseDto.builder()
                .token(tokenInfo.getToken())
                .refreshToken(refreshTokenEntity.getToken())
                .tokenType("Bearer")
                .expiresIn(tokenInfo.getExpiresIn())
                .jti(tokenInfo.getJti())
                .build();
    }

}
