package com.kevindai.oauth2server.dto.user.oauth2;

import com.kevindai.oauth2server.dto.user.UserInfoDto;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Data
public class CustomOidcUser implements OidcUser {
    private UserInfoDto userInfo;
    private Map<String, Object> claims;
    private Set<GrantedAuthority> authorities;
    private OidcIdToken oidcIdToken;
    private OidcUserInfo oidcUserInfo;

    public CustomOidcUser(UserInfoDto userInfo, Map<String, Object> claims, Collection<? extends GrantedAuthority> authorities, OidcIdToken oidcIdToken, OidcUserInfo oidcUserInfo) {
        this.userInfo = userInfo;
        this.claims = claims;
        this.authorities = Set.copyOf(authorities);
        this.oidcIdToken = oidcIdToken;
        this.oidcUserInfo = oidcUserInfo;
    }

    @Override
    public Map<String, Object> getClaims() {
        return claims;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUserInfo;
    }

    @Override
    public OidcIdToken getIdToken() {
        return oidcIdToken;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return claims;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return userInfo.getUsername();
    }

    public UserInfoDto getUserInfoDto() {
        return userInfo;
    }
}
