package com.nicky.rabobank.technical.assessment.service;

import com.nicky.rabobank.technical.assessment.model.User;
import com.nicky.rabobank.technical.assessment.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * Service class that handles business logic for user operations.
 * Manages user retrieval and creation functionality.
 */
@Service
public class UserService {

    /**
     * Repository for accessing user data.
     */
    private final UserRepository userRepository;

    /**
     * Constructs a new UserService with the required repository.
     *
     * @param userRepository repository for user operations
     */
    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all users from the database.
     *
     * @return an iterable collection of all users
     */
    public Iterable<User> getAll() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return the user if found
     * @throws NoSuchElementException if the user does not exist
     */
    public User get(final int id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("User not found with id: "
                        + id));
    }

    /**
     * Creates a new user in the database.
     *
     * @param user the user to create
     * @return the created user with its generated ID
     * @throws IllegalStateException    if a user with same ID already exists
     * @throws IllegalArgumentException if the user is null or invalid
     */
    @Transactional
    public User create(final User user) {
        if (user == null || user.getUserName() == null
                || user.getUserName().isEmpty()) {
            throw new IllegalArgumentException("Invalid user: user or "
                    + "username cannot be null or empty");
        }
        if (user.getId() != null && user.getId() != 0
                && userRepository.existsById(user.getId())) {
            throw new IllegalStateException("User with ID " + user.getId()
                    + " already exists");
        }
        return userRepository.save(user);
    }
}
