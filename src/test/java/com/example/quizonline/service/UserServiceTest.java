package com.example.quizonline.service;

import com.example.quizonline.exception.UserAlreadyExistsException;
import com.example.quizonline.model.Role;
import com.example.quizonline.model.User;
import com.example.quizonline.repository.RoleRepository;
import com.example.quizonline.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        role = new Role();
        role.setName("ROLE_ADMIN");
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User savedUser = userService.registerUser(user);

        assertNotNull(savedUser);
        assertEquals("encodedPassword", savedUser.getPassword());
        assertTrue(savedUser.getRoles().contains(role));
        verify(userRepository).save(user);
    }

    @Test
    void testRegisterUser_UserAlreadyExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testGetUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.getUsers();

        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
    }

    @Test
    void testGetUser_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User foundUser = userService.getUser(user.getEmail());

        assertEquals(user, foundUser);
    }

    @Test
    void testGetUser_NotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getUser(user.getEmail()));
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        userService.deleteUser(user.getEmail());

        verify(userRepository).deleteByEmail(user.getEmail());
    }

    @Test
    void testDeleteUser_UserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.deleteUser(user.getEmail()));
        verify(userRepository, never()).deleteByEmail(any());
    }
}
