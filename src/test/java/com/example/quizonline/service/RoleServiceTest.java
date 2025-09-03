package com.example.quizonline.service;

import com.example.quizonline.exception.RoleAlreadyExistException;
import com.example.quizonline.exception.UserAlreadyExistsException;
import com.example.quizonline.model.Role;
import com.example.quizonline.model.User;
import com.example.quizonline.repository.RoleRepository;
import com.example.quizonline.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.spy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    private RoleService roleService;

    private Role role1;
    private User user1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        roleService = spy(new RoleService(roleRepository, userRepository));

        role1 = new Role("ROLE_ADMIN");
        role1.setId(1L);

        user1 = new User();
        user1.setId(1L);
        user1.setFirstName("John");
        user1.setEmail("John");
    }

    // ================= getRoles =================
    @Test
    void testGetRoles() {
        when(roleRepository.findAll()).thenReturn(Arrays.asList(role1));

        List<Role> result = roleService.getRoles();

        assertEquals(1, result.size());
        verify(roleRepository, times(1)).findAll();
    }

    // ================= createRole =================
    @Test
    void testCreateRole_Success() {
        when(roleRepository.existsByName("ROLE_ADMIN")).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(role1);

        Role result = roleService.createRole(new Role("Admin"));

        assertEquals("ROLE_ADMIN", result.getName());
        verify(roleRepository, times(1)).existsByName("ROLE_ADMIN");
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void testCreateRole_AlreadyExists() {
        when(roleRepository.existsByName("ROLE_ADMIN")).thenReturn(true);

        RoleAlreadyExistException exception = assertThrows(RoleAlreadyExistException.class, () -> {
            roleService.createRole(new Role("Admin"));
        });

        assertEquals("Admin role already exists", exception.getMessage());
        verify(roleRepository, times(1)).existsByName("ROLE_ADMIN");
        verify(roleRepository, times(0)).save(any(Role.class));
    }

    // ================= deleteRole =================
    @Test
    void testDeleteRole() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role1));
        when(roleService.removeAllUserFromRole(1L)).thenReturn(role1);
        doNothing().when(roleRepository).deleteById(1L);

        roleService.deleteRole(1L);

        verify(roleService, times(1)).removeAllUserFromRole(1L);
        verify(roleRepository, times(1)).deleteById(1L);
    }

    // ================= findByName =================
    @Test
    void testFindByName() {
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(role1));

        Role result = roleService.findByName("ROLE_ADMIN");

        assertEquals(role1, result);
        verify(roleRepository, times(1)).findByName("ROLE_ADMIN");
    }

    // ================= removeUserFromRole =================
    @Test
    void testRemoveUserFromRole_Success() {
        role1.getUsers().add(user1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role1));
        when(roleRepository.save(any(Role.class))).thenReturn(role1);

        User result = roleService.removeUserFromRole(1L, 1L);

        assertEquals(user1, result);
        verify(roleRepository, times(1)).save(role1);
    }

    @Test
    void testRemoveUserFromRole_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role1));

        // role does not contain user
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            roleService.removeUserFromRole(1L, 1L);
        });

        assertEquals("User not found", exception.getMessage());
    }

    // ================= assignRoleToUser =================
    @Test
    void testAssignRoleToUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role1));
        when(roleRepository.save(any(Role.class))).thenReturn(role1);

        User result = roleService.assignRoleToUser(1L, 1L);

        assertEquals(user1, result);
        verify(roleRepository, times(1)).save(role1);
    }

    @Test
    void testAssignRoleToUser_AlreadyAssigned() {
        user1.getRoles().add(role1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role1));

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            roleService.assignRoleToUser(1L, 1L);
        });

        assertEquals("John is already assigned to the ROLE_ADMIN role", exception.getMessage());
    }

    // ================= removeAllUserFromRole =================
    @Test
    void testRemoveAllUserFromRole() {
        role1.getUsers().add(user1);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role1));
        when(roleRepository.save(any(Role.class))).thenReturn(role1);

        Role result = roleService.removeAllUserFromRole(1L);

        assertTrue(result.getUsers().isEmpty());
        verify(roleRepository, times(1)).save(role1);
    }
}
