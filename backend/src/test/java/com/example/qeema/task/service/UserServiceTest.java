package com.example.qeema.task.service;

import com.example.qeema.task.entity.User;
import com.example.qeema.task.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testSave() {
        User user = new User();
        user.setUsername("testuser");

        userService.save(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testFindByUsername_UserFound() {
        User mockUser = new User();
        mockUser.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        User user = userService.findByUsername("testuser");

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
    }

    @Test
    public void testFindByUsername_UserNotFound() {
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        User user = userService.findByUsername("nonexistentuser");

        assertNull(user);
    }
}
