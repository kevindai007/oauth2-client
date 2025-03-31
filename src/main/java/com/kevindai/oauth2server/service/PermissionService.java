package com.kevindai.oauth2server.service;

import com.kevindai.oauth2server.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PermissionService {
    private final MenuRepository menuRepository;

    public List<String> getPermissionsByUserId(Long userId) {
        return menuRepository.getPermissionsByUserId(userId);
    }
}
