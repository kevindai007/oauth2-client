package com.kevindai.oauth2server.service;

import com.kevindai.oauth2server.dto.common.JwtTokenInfo;
import com.kevindai.oauth2server.dto.user.UserInfoDto;
import com.kevindai.oauth2server.dto.user.UsersLoginRequestDto;
import com.kevindai.oauth2server.entity.RefreshTokenEntity;
import com.kevindai.oauth2server.entity.UsersEntity;
import com.kevindai.oauth2server.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {
    public static String SECRET_KEY = null;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtService(RefreshTokenRepository refreshTokenRepository) {
        try {
            KeyGenerator hmacSHA256 = KeyGenerator.getInstance("HmacSHA256");
            SecretKey secretKey = hmacSHA256.generateKey();
            SECRET_KEY = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public JwtTokenInfo generateAccessToken(UsersLoginRequestDto usersLoginRequestDto, List<String> permissions) {
        return generateToken(usersLoginRequestDto.getUsername(), permissions);
    }

    public JwtTokenInfo generateAccessToken(UserInfoDto user, List<String> permissions) {
        return generateToken(user.getUsername(), permissions);
    }

    private JwtTokenInfo generateToken(String username, List<String> permissions) {
        String jti = UUID.randomUUID().toString();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 1000 * 60 * 60 * 10); // 10 hours

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("permissions", permissions);

        String token = Jwts.builder()
                .claims(claims)
                .subject(username)
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
}
