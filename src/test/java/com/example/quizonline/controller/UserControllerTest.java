package com.example.quizonline.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import com.example.quizonline.model.User;
import com.example.quizonline.service.IUserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class UserControllerTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private UserController userController;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user1 = new User();
        user1.setId(1L);
        user1.setFirstName("user1");
        user1.setLastName("user1");
        user1.setEmail("user1@example.com");

        user2 = new User();
        user2.setId(2L);
        user2.setFirstName("user2");
        user2.setLastName("user2");
        user2.setEmail("user2@example.com");
    }

    @Test
    void testGetUsers() {
        List<User> mockUsers = Arrays.asList(user1, user2);
        when(userService.getUsers()).thenReturn(mockUsers);

        ResponseEntity<List<User>> response = userController.getUsers();

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).getUsers();
    }

    @Test
    void testGetUserByEmail_Success() {
        when(userService.getUser("1")).thenReturn(user1);

        ResponseEntity<?> response = userController.getUserByEmail("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user1, response.getBody());
        verify(userService, times(1)).getUser("1");
    }

    @Test
    void testGetUserByEmail_NotFound() {
        when(userService.getUser("3")).thenThrow(new UsernameNotFoundException("User not found"));

        ResponseEntity<?> response = userController.getUserByEmail("3");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(userService, times(1)).getUser("3");
    }

    @Test
    void testGetUserByEmail_OtherException() {
        when(userService.getUser("1")).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = userController.getUserByEmail("1");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error fetching user", response.getBody());
        verify(userService, times(1)).getUser("1");
    }

    @Test
    void testDeleteUser_Success() {
        doNothing().when(userService).deleteUser("user1@example.com");

        ResponseEntity<String> response = userController.deleteUser("user1@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User deleted successfully", response.getBody());
        verify(userService, times(1)).deleteUser("user1@example.com");
    }

    @Test
    void testDeleteUser_NotFound() {
        doThrow(new UsernameNotFoundException("User not found")).when(userService).deleteUser("user3@example.com");

        ResponseEntity<String> response = userController.deleteUser("user3@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(userService, times(1)).deleteUser("user3@example.com");
    }

    @Test
    void testDeleteUser_OtherException() {
        doThrow(new RuntimeException("Database error")).when(userService).deleteUser("user1@example.com");

        ResponseEntity<String> response = userController.deleteUser("user1@example.com");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error deleting user: Database error", response.getBody());
        verify(userService, times(1)).deleteUser("user1@example.com");
    }
}
