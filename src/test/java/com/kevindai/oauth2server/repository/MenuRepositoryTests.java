package com.kevindai.oauth2server.repository;

import com.kevindai.oauth2server.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MenuRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleMenuRepository roleMenuRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private MenuRepository menuRepository;

    private Long userId;
    private Integer roleId;
    private Integer menuId1;
    private Integer menuId2;

    @BeforeEach
    void setUp() {
        tearDown();
        UsersEntity superAdmin = new UsersEntity();
        superAdmin.setUsername("superadmin");
        userRepository.save(superAdmin);
        userId = superAdmin.getId();

        RoleEntity superAdminRole = new RoleEntity();
        superAdminRole.setName("SUPER_ADMIN");
        roleRepository.save(superAdminRole);
        roleId = superAdminRole.getId();

        MenuEntity menu1 = new MenuEntity();
        menu1.setRequiredPermission("PERMISSION_1");
        menu1.setName("Menu 1");
        menu1.setOrderIndex(1);
        menuRepository.save(menu1);
        menuId1 = menu1.getId();

        MenuEntity menu2 = new MenuEntity();
        menu2.setRequiredPermission("PERMISSION_2");
        menu2.setName("Menu 2");
        menu2.setOrderIndex(2);
        menuRepository.save(menu2);
        menuId2 = menu2.getId();

        RoleMenuEntity roleMenu1 = new RoleMenuEntity();
        roleMenu1.setRoleId(roleId);
        roleMenu1.setMenuId(menuId1);
        roleMenuRepository.save(roleMenu1);

        RoleMenuEntity roleMenu2 = new RoleMenuEntity();
        roleMenu2.setRoleId(roleId);
        roleMenu2.setMenuId(menuId2);
        roleMenuRepository.save(roleMenu2);

        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setUserId(Math.toIntExact(userId));
        userRole.setRoleId(roleId);
        userRoleRepository.save(userRole);
    }


    void tearDown() {
        userRoleRepository.deleteAll();
        roleMenuRepository.deleteAll();
        menuRepository.deleteAll();
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Get permissions by user ID returns correct permissions")
    void getPermissionsByUserIdReturnsCorrectPermissions() {
        List<String> permissions = menuRepository.getPermissionsByUserId(userId);

        assertThat(permissions).containsExactlyInAnyOrder("PERMISSION_1", "PERMISSION_2");
    }

    @Test
    @DisplayName("Get permissions by user ID returns empty list for non-existent user")
    void getPermissionsByUserIdReturnsEmptyListForNonExistentUser() {
        List<String> permissions = menuRepository.getPermissionsByUserId(999L);

        assertThat(permissions).isEmpty();
    }

    @Test
    @DisplayName("Get permissions by user ID returns empty list for user with no roles")
    void getPermissionsByUserIdReturnsEmptyListForUserWithNoRoles() {
        List<String> permissions = menuRepository.getPermissionsByUserId(2L);

        assertThat(permissions).isEmpty();
    }
}
