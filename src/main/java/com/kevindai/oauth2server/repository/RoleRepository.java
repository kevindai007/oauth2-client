package com.kevindai.oauth2server.repository;

import com.kevindai.oauth2server.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
    @Query("""
            select r from RoleEntity r inner join UserRoleEntity ur on r.id = ur.roleId
            where ur.userId = :userId
            """)
    List<RoleEntity> findByUserId(Long userId);
}
