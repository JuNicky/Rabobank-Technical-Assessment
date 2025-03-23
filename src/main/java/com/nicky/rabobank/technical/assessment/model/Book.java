package com.nicky.rabobank.technical.assessment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Entity class representing a book in the library system.
 * Contains book details and availability information.
 */
@Entity
@Table(name = "books")
public final class Book {

    /**
     * Unique identifier for the book.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Title of the book. Cannot be null.
     */
    @NotNull(message = "Title cannot be null")
    private String title;

    /**
     * Author of the book. Cannot be null.
     */
    @NotNull(message = "Author cannot be null")
    private String author;

    /**
     * Flag indicating if the book is available for borrowing.
     * Defaults to true.
     */
    private boolean isAvailable = true;

    /**
     * ID of the user who has currently borrowed this book.
     * Null if the book is not borrowed.
     */
    private Integer borrowerId;

    /**
     * Gets the book's unique identifier.
     *
     * @return the book ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the book's unique identifier.
     *
     * @param id the book ID to set
     */
    public void setId(final Integer id) {
        this.id = id;
    }

    /**
     * Gets the book's title.
     *
     * @return the title of the book
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the book's title.
     *
     * @param title the title to set
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Gets the book's author.
     *
     * @return the author of the book
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the book's author.
     *
     * @param author the author to set
     */
    public void setAuthor(final String author) {
        this.author = author;
    }

    /**
     * Checks if the book is available for borrowing.
     *
     * @return true if the book is available, false otherwise
     */
    public boolean isAvailable() {
        return isAvailable;
    }

    /**
     * Sets the availability status of the book.
     *
     * @param available the availability status to set
     */
    public void setAvailable(final boolean available) {
        isAvailable = available;
    }

    /**
     * Gets the ID of the user who has borrowed this book.
     *
     * @return the borrower's ID, or null if not borrowed
     */
    public Integer getBorrowerId() {
        return borrowerId;
    }

    /**
     * Sets the ID of the user who has borrowed this book.
     *
     * @param borrowerId the borrower's ID to set, or null if returned
     */
    public void setBorrowerId(final Integer borrowerId) {
        this.borrowerId = borrowerId;
    }
}
