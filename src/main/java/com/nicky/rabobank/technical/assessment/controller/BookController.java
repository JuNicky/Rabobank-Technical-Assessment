package com.nicky.rabobank.technical.assessment.controller;

import com.nicky.rabobank.technical.assessment.model.Book;
import com.nicky.rabobank.technical.assessment.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/books")
public class BookController {

    /**
     * Service for handling book operations.
     */
    private final BookService bookService;

    /**
     * Constructs a new BookController with the specified BookService.
     *
     * @param bookService the service to handle book operations
     */
    public BookController(final BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Retrieves all books.
     *
     * @return a ResponseEntity containing an iterable collection of all books
     */
    @GetMapping()
    public ResponseEntity<Iterable<Book>> getAll() {
        return ResponseEntity.ok(bookService.getAll());
    }

    /**
     * Retrieves a book by its ID.
     *
     * @param id the ID of the book to retrieve
     * @return a ResponseEntity containing the book with the specified ID
     * @throws ResponseStatusException if the book doesn't exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable final int id) {
        Book book = bookService.get(id);
        if (book == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Book not found with id: " + id);
        }
        return ResponseEntity.ok(book);
    }

    /**
     * Retrieves all books borrowed by a specific user.
     *
     * @param userId the ID of the user whose books are to be retrieved
     * @return a ResponseEntity containing a list of books borrowed by the user
     * @throws ResponseStatusException if the user doesn't exist
     *                                 or has no books
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Book>> getBooksByUserId(
            @PathVariable final int userId) {
        try {
            List<Book> books = bookService.getBooksByUserId(userId);
            return ResponseEntity.ok(books);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());
        }
    }

    /**
     * Searches for books by title and/or author.
     *
     * @param title  the title to search for (optional)
     * @param author the author to search for (optional)
     * @return a ResponseEntity containing a list of matching books
     * @throws ResponseStatusException if neither title nor
     *                                 author is provided
     */
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam(required = false) final String title,
            @RequestParam(required = false) final String author) {
        // If both parameters are empty, return a bad request
        if ((title == null || title.trim().isEmpty())
                && (author == null || author.trim().isEmpty())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "At least one search parameter (title or author) must be "
                            + "provided");
        }

        List<Book> books = bookService.searchBooks(title, author);

        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(books);
    }

    /**
     * Creates a new book.
     *
     * @param book the book to create
     * @return a ResponseEntity containing the created book
     */
    @PostMapping()
    public ResponseEntity<Book> create(@RequestBody @Valid final Book book) {
        bookService.create(book);
        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    /**
     * Updates an existing book.
     *
     * @param id   the ID of the book to update
     * @param book the updated book data
     * @return a ResponseEntity containing the updated book
     * @throws ResponseStatusException if the book doesn't exist
     */
    @PutMapping("/{id}")
    public ResponseEntity<Book> update(
            @PathVariable final int id, @RequestBody @Valid final Book book) {
        if (bookService.get(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Book not found with id: " + id);
        }
        book.setId(id); // ID is not set in the object, so we assign it here

        Book updated = bookService.update(book);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a book by its ID.
     *
     * @param id the ID of the book to delete
     * @return a ResponseEntity
     * @throws ResponseStatusException if the book doesn't exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable final int id) {
        if (bookService.get(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Book not found with id: " + id);
        }
        bookService.remove(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Marks a book as borrowed by a specific user.
     *
     * @param id     the ID of the book to borrow
     * @param userId the ID of the user borrowing the book
     * @return a ResponseEntity containing the borrowed book
     * @throws ResponseStatusException if the book or user doesn't exist, or
     *                                 if the book  is already borrowed
     */
    @PutMapping("/borrow/{id}/{userId}")
    public ResponseEntity<Book> borrowBook(
            @PathVariable final int id, @PathVariable final int userId) {
        try {
            Book borrowedBook = bookService.borrowBook(id, userId);
            return ResponseEntity.ok(borrowedBook);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Marks a book as returned.
     *
     * @param id the ID of the book to return
     * @return a ResponseEntity containing the returned book
     * @throws ResponseStatusException if the book doesn't exist, or if the
     *                                 book is not currently borrowed
     */
    @PutMapping("/return/{id}")
    public ResponseEntity<Book> returnBook(@PathVariable final int id) {
        try {
            Book returnedBook = bookService.returnBook(id);
            return ResponseEntity.ok(returnedBook);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
