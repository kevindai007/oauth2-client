package com.kevindai.oauth2server.config;

import com.kevindai.oauth2server.dto.common.JwtTokenInfo;
import com.kevindai.oauth2server.dto.user.UserInfoDto;
import com.kevindai.oauth2server.dto.user.oauth2.CustomOAuth2User;
import com.kevindai.oauth2server.dto.user.oauth2.CustomOidcUser;
import com.kevindai.oauth2server.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserInfoDto userInfoDto = null;

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomOidcUser) {
            CustomOidcUser oidcUser = (CustomOidcUser) principal;
            userInfoDto = oidcUser.getUserInfoDto();
            log.info("OIDC login - email: {}, username: {}", userInfoDto.getEmail(), oidcUser.getName());
        } else if (principal instanceof CustomOAuth2User) {
            CustomOAuth2User oauth2User = (CustomOAuth2User) principal;
            log.info("OAuth2 login - email: {}, username: {}", oauth2User.getUserInfo().getEmail(), oauth2User.getName());
            userInfoDto = oauth2User.getUserInfo();
        } else {
            log.warn("Unknown authentication principal type: {}", principal.getClass());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
            return;

        }
        JwtTokenInfo tokenInfo = jwtService.generateToken(userInfoDto.getId());


        //Option A: redirect with token
        String redirectUrl = "/index.html?token=" + tokenInfo.getToken();
        response.sendRedirect(redirectUrl);

        // OPTION 2: If using a front-end REST client
        // response.setContentType("application/json");
        // response.getWriter().write(JsonUtil.toJson(jwtTokenInfo).orElseThrow(() -> new RuntimeException("Failed to convert to JSON")));
    }

}

