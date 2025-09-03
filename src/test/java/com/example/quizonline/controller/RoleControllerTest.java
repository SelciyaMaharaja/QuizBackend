package com.example.quizonline.controller;

import com.example.quizonline.exception.RoleAlreadyExistException;
import com.example.quizonline.model.Role;
import com.example.quizonline.model.User;
import com.example.quizonline.service.IRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleControllerTest {

    @Mock
    private IRoleService roleService;

    @InjectMocks
    private RoleController roleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------- all-roles ----------
    @Test
    void testGetAllRoles() {
        List<Role> roles = List.of(new Role("ROLE_ADMIN"), new Role("ROLE_USER"));
        when(roleService.getRoles()).thenReturn(roles);

        ResponseEntity<List<Role>> response = roleController.getAllRoles();

        assertEquals(302, response.getStatusCodeValue()); // HttpStatus.FOUND
        assertEquals(2, response.getBody().size());
        verify(roleService, times(1)).getRoles();
    }

    // ---------- create-new-role ----------
    @Test
    void testCreateRole_Success() {
        Role role = new Role("ROLE_ADMIN");
        when(roleService.createRole(role)).thenReturn(role);

        ResponseEntity<String> response = roleController.createRole(role);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("New role created successfully!", response.getBody());
        verify(roleService, times(1)).createRole(role);
    }

    @Test
    void testCreateRole_RoleAlreadyExists() {
        Role role = new Role();
        doThrow(new RoleAlreadyExistException("Role already exists"))
                .when(roleService).createRole(role);

        ResponseEntity<String> response = roleController.createRole(role);

        assertEquals(409, response.getStatusCodeValue()); // HttpStatus.CONFLICT
        assertEquals("Role already exists", response.getBody());
        verify(roleService, times(1)).createRole(role);
    }

    // ---------- delete ----------
    @Test
    void testDeleteRole() {
        doNothing().when(roleService).deleteRole(1L);

        roleController.deleteRole(1L);

        verify(roleService, times(1)).deleteRole(1L);
    }

    // ---------- remove all users from role ----------
    @Test
    void testRemoveAllUsersFromRole() {
        Role role = new Role();
        role.setId(1L);

        when(roleService.removeAllUserFromRole(1L)).thenReturn(role);

        Role response = roleController.removeAllUsersFromRole(1L);

        assertEquals(1L, response.getId());
        verify(roleService, times(1)).removeAllUserFromRole(1L);
    }

    // ---------- remove user from role ----------
    @Test
    void testRemoveUserFromRole() {
        User user = new User();
        user.setId(100L);

        when(roleService.removeUserFromRole(100L, 1L)).thenReturn(user);

        User response = roleController.removeUserFromRole(100L, 1L);

        assertEquals(100L, response.getId());
        verify(roleService, times(1)).removeUserFromRole(100L, 1L);
    }

    // ---------- assign user to role ----------
    @Test
    void testAssignUserToRole() {
        User user = new User();
        user.setId(200L);

        when(roleService.assignRoleToUser(200L, 2L)).thenReturn(user);

        User response = roleController.assignUserToRole(200L, 2L);

        assertEquals(200L, response.getId());
        verify(roleService, times(1)).assignRoleToUser(200L, 2L);
    }
}
