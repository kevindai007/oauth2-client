package com.kevindai.oauth2server.controller;

import com.kevindai.oauth2server.entity.Oauth2ClientConfigEntity;
import com.kevindai.oauth2server.repository.Oauth2ClientConfigRepository;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class TestController {
    private final Oauth2ClientConfigRepository oauth2ClientConfigRepository;


    @RequestMapping("/hello")
    public String hello() {
        List<Oauth2ClientConfigEntity> all = oauth2ClientConfigRepository.findAll();
        return "Hello, World!";
    }
}
