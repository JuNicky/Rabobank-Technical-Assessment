package com.nicky.rabobank.technical.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicky.rabobank.technical.assessment.model.User;
import com.nicky.rabobank.technical.assessment.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private List<User> userList;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUserName("Nicky Test User 1");

        User secondUser = new User();
        secondUser.setId(2);
        secondUser.setUserName("Nicky Test User 2");

        userList = Arrays.asList(testUser, secondUser);
    }

    @Test
    void getAllUsers_ReturnsAllUsers() throws Exception {
        when(userService.getAll()).thenReturn(userList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userName").value("Nicky Test User 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].userName").value("Nicky Test User 2"));
    }

    @Test
    void getUserById_ReturnsUser() throws Exception {
        when(userService.get(1)).thenReturn(testUser);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userName").value("Nicky Test User 1"));
    }

    @Test
    void getUserById_UserNotFound_ReturnsNotFound() throws Exception {
        when(userService.get(999)).thenReturn(null);

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_WithValidData_ReturnsCreated() throws Exception {
        when(userService.create(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userName").value("Nicky Test User 1"));
    }
}