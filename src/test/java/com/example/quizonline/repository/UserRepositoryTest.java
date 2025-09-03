package com.example.quizonline.repository;

import com.example.quizonline.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User user1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@example.com");
        user1.setFirstName("user1");
        user1.setLastName("user1");
    }

    @Test
    void testExistsByEmail() {
        when(userRepository.existsByEmail("user1@example.com")).thenReturn(true);

        boolean exists = userRepository.existsByEmail("user1@example.com");

        assertTrue(exists);
        verify(userRepository, times(1)).existsByEmail("user1@example.com");
    }

    @Test
    void testFindByEmail() {
        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(user1));

        Optional<User> result = userRepository.findByEmail("user1@example.com");

        assertTrue(result.isPresent());
        assertEquals("user1@example.com", result.get().getEmail());
        verify(userRepository, times(1)).findByEmail("user1@example.com");
    }

    @Test
    void testDeleteByEmail() {
        doNothing().when(userRepository).deleteByEmail("user1@example.com");

        userRepository.deleteByEmail("user1@example.com");

        verify(userRepository, times(1)).deleteByEmail("user1@example.com");
    }
}
