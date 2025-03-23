package com.nicky.rabobank.technical.assessment.service;

import com.nicky.rabobank.technical.assessment.model.User;
import com.nicky.rabobank.technical.assessment.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    private User secondUser;

    private List<User> userList;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUserName("Nicky Test User 1");

        secondUser = new User();
        secondUser.setId(2);
        secondUser.setUserName("Nicky Test User 2");

        userList = Arrays.asList(testUser, secondUser);
    }

    @Test
    void getAll_ReturnsAllUsers() {
        when(userRepository.findAll()).thenReturn(userList);

        Iterable<User> result = userService.getAll();

        assertEquals(userList, result);
    }

    @Test
    void get_WithValidId_ReturnsUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        User result = userService.get(1);

        assertEquals(testUser, result);
    }

    @Test
    void get_WithInvalidId_ReturnsNull() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            userService.get(999);
        });

        assertTrue(exception.getMessage().contains("User not found with id: 999"));
    }

    @Test
    void create_WithNewUser_SavesUser() {
        testUser.setId(1);
        when(userRepository.existsById(1)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.create(testUser);

        assertEquals(testUser, result);
        verify(userRepository, times(1)).existsById(1);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void create_WithNullId_SavesUserWithoutExistsCheck() {
        testUser.setId(null);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.create(testUser);

        assertEquals(testUser, result);
        verify(userRepository, never()).existsById(anyInt());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void create_WithZeroId_SavesUserWithoutExistsCheck() {
        testUser.setId(0);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.create(testUser);

        assertEquals(testUser, result);
        verify(userRepository, never()).existsById(anyInt());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void create_WithExistingUserId_ThrowsException() {
        testUser.setId(1);
        when(userRepository.existsById(1)).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            userService.create(testUser);
        });

        assertTrue(exception.getMessage().contains("User with ID 1 already exists"));
        verify(userRepository, times(1)).existsById(1);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void create_WithNullUser_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.create(null);
        });

        assertTrue(exception.getMessage().contains("user or username cannot be null or empty"));
        verify(userRepository, never()).existsById(anyInt());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void create_WithNullUsername_ThrowsException() {
        User invalidUser = new User();
        invalidUser.setId(1);
        invalidUser.setUserName(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.create(invalidUser);
        });

        assertTrue(exception.getMessage().contains("user or username cannot be null or empty"));
        verify(userRepository, never()).existsById(anyInt());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void create_WithEmptyUsername_ThrowsException() {
        User invalidUser = new User();
        invalidUser.setId(1);
        invalidUser.setUserName("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.create(invalidUser);
        });

        assertTrue(exception.getMessage().contains("user or username cannot be null or empty"));
        verify(userRepository, never()).existsById(anyInt());
        verify(userRepository, never()).save(any(User.class));
    }
}