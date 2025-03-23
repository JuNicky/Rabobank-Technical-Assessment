package com.nicky.rabobank.technical.assessment.controller;

import com.nicky.rabobank.technical.assessment.model.User;
import com.nicky.rabobank.technical.assessment.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users")
public final class UserController {

    /**
     * Service for handling user operations.
     */
    private final UserService userService;

    /**
     * Constructs a new UserController with the specified UserService.
     *
     * @param userService the service to handle user operations
     */
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves all users.
     *
     * @return a ResponseEntity containing an iterable collection of all users
     */
    @GetMapping()
    public ResponseEntity<Iterable<User>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return a ResponseEntity containing the user with the specified ID
     * @throws ResponseStatusException if the user doesn't exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable final int id) {
        User user = userService.get(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User not found with id: " + id);
        }
        return ResponseEntity.ok(user);
    }

    /**
     * Creates a new user.
     *
     * @param user the user to create
     * @return a ResponseEntity containing the created user
     */
    @PostMapping()
    public ResponseEntity<User> create(@RequestBody @Valid final User user) {
        userService.create(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
