package com.kevindai.oauth2server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "oauth2_client_config")
public class Oauth2ClientConfigEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column(name = "client_id", nullable = false, unique = true)
    private String clientId;
    @Column(name = "client_secret", nullable = false)
    private String clientSecret;
    @Column(name = "client_name")
    private String clientName;
    @Column(name = "authorization_grant_types", columnDefinition = "text[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> authorizationGrantTypes;
    @Column(name = "redirect_uris")
    private String redirectUris;
    @Column(name = "scopes", columnDefinition = "text[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> scopes;
    @Column(name = "require_authorization_consent")
    private Boolean requireAuthorizationConsent;
    @Column(name = "enabled")
    private Boolean enabled;
    @CreatedDate
    @Column(name = "created_time")
    private String createdTime;
    @Column(name = "updated_time")
    @LastModifiedDate
    private String updatedTime;
}
