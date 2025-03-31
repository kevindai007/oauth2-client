package com.kevindai.oauth2server.repository;

import com.kevindai.oauth2server.entity.RoleMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleMenuRepository extends JpaRepository<RoleMenuEntity, Integer> {
}
