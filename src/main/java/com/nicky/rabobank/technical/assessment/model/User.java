package com.nicky.rabobank.technical.assessment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Entity class representing a user in the library system.
 * Contains user identity and authentication details.
 */
@Entity
@Table(name = "users")
public final class User {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Username for the user. Cannot be null.
     */
    @NotNull(message = "Username cannot be null")
    private String userName;

    /**
     * Gets the username of the user.
     *
     * @return the username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the username of the user.
     *
     * @param userName the username to set
     */
    public void setUserName(final String userName) {
        this.userName = userName;
    }

    /**
     * Gets the user's unique identifier.
     *
     * @return the user ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the user's unique identifier.
     *
     * @param id the user ID to set
     */
    public void setId(final Integer id) {
        this.id = id;
    }
}
