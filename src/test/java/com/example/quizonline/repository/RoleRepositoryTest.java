package com.example.quizonline.repository;

import com.example.quizonline.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleRepositoryTest {

    @Mock
    private RoleRepository roleRepository;

    private Role role1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        role1 = new Role();
        role1.setId(1L);
        role1.setName("ROLE_USER");
    }

    @Test
    void testFindByName() {
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role1));

        Optional<Role> result = roleRepository.findByName("ROLE_USER");

        assertTrue(result.isPresent());
        assertEquals("ROLE_USER", result.get().getName());
        verify(roleRepository, times(1)).findByName("ROLE_USER");
    }

    @Test
    void testExistsByName() {
        when(roleRepository.existsByName("ROLE_USER")).thenReturn(true);

        boolean exists = roleRepository.existsByName("ROLE_USER");

        assertTrue(exists);
        verify(roleRepository, times(1)).existsByName("ROLE_USER");
    }
}
