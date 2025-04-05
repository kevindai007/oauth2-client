package com.kevindai.oauth2server.service;

import com.kevindai.base.projcore.exception.BizException;
import com.kevindai.oauth2server.dto.common.JwtTokenInfo;
import com.kevindai.oauth2server.entity.RefreshTokenEntity;
import com.kevindai.oauth2server.entity.RoleEntity;
import com.kevindai.oauth2server.entity.UsersEntity;
import com.kevindai.oauth2server.exception.AuthErrorCode;
import com.kevindai.oauth2server.repository.RefreshTokenRepository;
import com.kevindai.oauth2server.repository.RoleRepository;
import com.kevindai.oauth2server.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class JwtService {
    @Value("${jwt.secret-key}")
    private String SECRET_KEY;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PermissionService permissionService;
    private final RoleRepository roleRepository;

    public JwtTokenInfo generateToken(Long userId) {
        UsersEntity usersEntity = userRepository.findById(userId).orElseThrow(() -> new BizException(AuthErrorCode.INVALID_ID_USER_INFO.getErrorCode(), AuthErrorCode.INVALID_ID_USER_INFO.getHttpStatusCode(), AuthErrorCode.INVALID_ID_USER_INFO.getMessage()));
        List<String> permissions = permissionService.getPermissionsByUserId(userId);
        List<RoleEntity> roleEntities = roleRepository.findByUserId(userId);
        List<String> roles = roleEntities.stream().map(RoleEntity::getName).toList();

        String jti = UUID.randomUUID().toString();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 1000 * 60 * 60 * 10); // 10 hours

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", usersEntity.getUsername());
        claims.put("permissions", permissions);
        claims.put("roles", roles);

        String token = Jwts.builder()
                .claims(claims)
                .subject(usersEntity.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .id(jti)
                .signWith(getKey())
                .compact();

        return JwtTokenInfo.builder()
                .token(token)
                .expiresIn(expiryDate.getTime() - now.getTime())
                .jti(jti)
                .build();
    }

    private SecretKey getKey() {
        byte[] decode = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(decode);
    }

    public RefreshTokenEntity generateRefreshToken(UsersEntity user, List<String> permissions) {
        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUserId(Math.toIntExact(user.getId()));
        refreshToken.setClientId(null);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(30).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());  // Set refresh token to expire in 30 days
        refreshToken.setScope(String.join(" ", permissions));

        return refreshTokenRepository.save(refreshToken);
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public static void main(String[] args) throws Exception {
        KeyGenerator hmacSHA256 = KeyGenerator.getInstance("HmacSHA256");
        SecretKey secretKey = hmacSHA256.generateKey();
        System.out.println(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
    }
}
