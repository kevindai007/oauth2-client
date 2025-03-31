package com.kevindai.oauth2server.controller;

import com.kevindai.oauth2server.dto.user.UserInfoDto;
import com.kevindai.oauth2server.dto.user.UsersLoginRequestDto;
import com.kevindai.oauth2server.dto.user.UsersLoginResponseDto;
import com.kevindai.oauth2server.entity.UsersEntity;
import com.kevindai.oauth2server.service.UsersService;
import com.kevindai.oauth2server.service.biz.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class UsersController {
    private final UserService userService;
    private final UsersService usersService;


    @GetMapping("/users")
    public List<UsersEntity> findAll() {
        return userService.findAll();
    }

    /**
     * get current user info
     * @return
     */
    @GetMapping("/user")
    public UserInfoDto getUserInfo() {
        return userService.getCurrentUser();
    }

    @PostMapping("/login")
    public UsersLoginResponseDto login(@RequestBody @Valid UsersLoginRequestDto usersLoginRequestDto) {
        return usersService.login(usersLoginRequestDto);
    }
}
