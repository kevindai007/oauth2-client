package com.kevindai.oauth2server.dto.user.oauth2;


import com.kevindai.oauth2server.dto.user.UserInfoDto;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * this class is used to store the user info from oauth2 provider
 */
@Data
public class CustomOAuth2User implements OAuth2User {
    private UserInfoDto userInfo;
    private Map<String, Object> attributes;

    public CustomOAuth2User(UserInfoDto userInfo, Map<String, Object> attributes) {
        this.userInfo = userInfo;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return userInfo.getUsername();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // Or populate if needed
    }
}
