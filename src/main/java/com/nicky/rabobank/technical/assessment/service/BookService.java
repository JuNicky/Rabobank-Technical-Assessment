package com.nicky.rabobank.technical.assessment.service;

import com.nicky.rabobank.technical.assessment.model.Book;
import com.nicky.rabobank.technical.assessment.repository.BookRepository;
import com.nicky.rabobank.technical.assessment.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service class that handles business logic for book operations.
 * Manages book retrieval, creation, updates, and borrowing functionality.
 */
@Service
public class BookService {

    /**
     * Repository for accessing book data.
     */
    private final BookRepository bookRepository;

    /**
     * Repository for accessing user data.
     */
    private final UserRepository userRepository;

    /**
     * Constructs a new BookService with the required repositories.
     *
     * @param bookRepository repository for book operations
     * @param userRepository repository for user operations
     */
    public BookService(final BookRepository bookRepository,
                       final UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all books from the database.
     *
     * @return an iterable collection of all books
     */
    public Iterable<Book> getAll() {
        return bookRepository.findAll();
    }

    /**
     * Retrieves a book by its ID.
     *
     * @param id the ID of the book to retrieve
     * @return the book if found
     * @throws NoSuchElementException if the book does not exist
     */
    public Book get(final int id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found "
                        + "with id: " + id));
    }

    /**
     * Retrieves all books borrowed by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of books borrowed by the user
     * @throws NoSuchElementException if the user doesn't exist
     */
    public List<Book> getBooksByUserId(final int userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User not found with id: "
                    + userId);
        }
        return bookRepository.findByBorrowerId(userId);
    }

    /**
     * Searches for books by title and/or author.
     *
     * @param title the title to search for (optional)
     * @param author the author to search for (optional)
     * @return a list of books matching the search criteria
     * @throws IllegalArgumentException if neither title nor author is provided
     */
    public List<Book> searchBooks(final String title, final String author) {
        // Sanitize inputs by converting null to empty string and trimming
        String sanitizedTitle = (title != null) ? title.trim() : "";
        String sanitizedAuthor = (author != null) ? author.trim() : "";

        // Check if at least one parameter is provided after sanitization
        if (sanitizedTitle.isEmpty() && sanitizedAuthor.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one search parameter (title or author) must be "
                            + "provided");
        }

        return bookRepository
                .findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(
                        sanitizedTitle, sanitizedAuthor
                );
    }

    /**
     * Creates a new book in the database.
     *
     * @param book the book to create
     * @return the created book with its generated ID
     * @throws IllegalStateException if a book with the same ID already exists
     */
    @Transactional
    public Book create(final Book book) {
        if (book.getId() != null && book.getId() != 0
                && bookRepository.existsById(book.getId())) {
            throw new IllegalStateException("Book with ID " + book.getId()
                    + " already exists");
        }
        return bookRepository.save(book);
    }

    /**
     * Removes a book from the database.
     *
     * @param id the ID of the book to remove
     */
    @Transactional
    public void remove(final int id) {
        bookRepository.deleteById(id);
    }


    /**
     * Updates an existing book's information.
     *
     * @param book the book with updated information
     * @return the updated book
     * @throws NoSuchElementException if the book doesn't exist
     */
    @Transactional
    public Book update(final Book book) {
        Book existingBook = bookRepository.findById(book.getId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Book with ID " + book.getId() + " not found"));

        // Only update title and author
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());

        return bookRepository.save(existingBook);
    }

    /**
     * Marks a book as borrowed by a specific user.
     *
     * @param id the ID of the book to borrow
     * @param userId the ID of the user borrowing the book
     * @return the updated book with borrower information
     * @throws NoSuchElementException if the book or user doesn't exist
     * @throws IllegalStateException if the book is already borrowed
     */
    @Transactional
    public Book borrowBook(final int id, final int userId) {
        // First check if the user exists
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User not found with id: "
                    + userId);
        }

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Book not found with id: " + id));

        if (!book.isAvailable()) {
            throw new IllegalStateException("Book is already borrowed");
        }

        book.setAvailable(false);
        book.setBorrowerId(userId);
        return bookRepository.save(book);
    }

    /**
     * Marks a book as returned (available for borrowing).
     *
     * @param id the ID of the book to return
     * @return the updated book
     * @throws NoSuchElementException if the book doesn't exist
     * @throws IllegalStateException if the book is not currently borrowed
     */
    @Transactional
    public Book returnBook(final int id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Book not found with id: " + id));

        if (book.isAvailable()) {
            throw new IllegalStateException("Book is not currently borrowed");
        }

        book.setAvailable(true);
        book.setBorrowerId(null);
        return bookRepository.save(book);
    }
}
