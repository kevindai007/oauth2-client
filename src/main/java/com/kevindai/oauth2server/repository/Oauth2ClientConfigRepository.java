package com.kevindai.oauth2server.repository;

import com.kevindai.oauth2server.entity.Oauth2ClientConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Oauth2ClientConfigRepository extends JpaRepository<Oauth2ClientConfigEntity, Long> {
    Oauth2ClientConfigEntity findByClientId(String clientId);
}
