package com.kevindai.oauth2server.service.oauth2;

import com.kevindai.oauth2server.dto.user.UserInfoDto;
import com.kevindai.oauth2server.dto.user.oauth2.CustomOidcUser;
import com.kevindai.oauth2server.service.biz.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final UserService userService;

    /**
     * google is oidc
     *
     * @param userRequest
     * @return
     * @throws OAuth2AuthenticationException
     */
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUserService delegate = new OidcUserService();
        OidcUser oidcUser = delegate.loadUser(userRequest);

        String email = oidcUser.getEmail();
        String username = oidcUser.getFullName(); // Or oidcUser.getPreferredUsername(), etc.

        log.info("OIDC login - email: {}, username: {}", email, username);

        UserInfoDto userInfo = userService.getUserInfo(username);
        if (userInfo == null) {
            userInfo = userService.createUserInfo(username, "google", email);
        }

        return new CustomOidcUser(userInfo, oidcUser.getClaims(), oidcUser.getAuthorities(), oidcUser.getIdToken(), oidcUser.getUserInfo());
    }
}
