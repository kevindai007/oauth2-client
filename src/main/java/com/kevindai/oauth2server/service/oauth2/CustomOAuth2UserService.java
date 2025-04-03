package com.kevindai.oauth2server.service.oauth2;

import com.kevindai.oauth2server.dto.user.UserInfoDto;
import com.kevindai.oauth2server.dto.user.oauth2.CustomOAuth2User;
import com.kevindai.oauth2server.service.biz.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserService userService;

    /**
     * github is oauth2
     *
     * @param userRequest
     * @return
     * @throws OAuth2AuthenticationException
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "google", "github"
        log.info("OAuth2 login via {}", registrationId);

        String username;
        String email;

        switch (registrationId) {
            case "github" -> {
                username = oauth2User.getAttribute("login");
                email = oauth2User.getAttribute("email");
                if (email == null) email = username + "@github.com";
            }
            case "kevindai" ->{
                username = oauth2User.getAttribute("username");
                email = oauth2User.getAttribute("email");
            }
            default -> throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        }

        // Save to DB or get existing user
        UserInfoDto userInfo = userService.getUserInfo(username);
        if (userInfo == null) {
            userInfo = userService.createUserInfo(username, registrationId, email);
        }

        // Return custom user that wraps DB info + OAuth attributes
        return new CustomOAuth2User(userInfo, oauth2User.getAttributes());
    }
}

