package com.nicky.rabobank.technical.assessment.service;

import com.nicky.rabobank.technical.assessment.model.Book;
import com.nicky.rabobank.technical.assessment.model.User;
import com.nicky.rabobank.technical.assessment.repository.BookRepository;
import com.nicky.rabobank.technical.assessment.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookService bookService;

    private Book firstTestBook;
    private Book secondTestBook;
    private List<Book> bookList;
    private User testUser;

    @BeforeEach
    void setUp() {
        firstTestBook = new Book();
        firstTestBook.setId(1);
        firstTestBook.setTitle("Test Book");
        firstTestBook.setAuthor("Test Author");

        secondTestBook = new Book();
        secondTestBook.setId(2);
        secondTestBook.setTitle("Another Test Book");
        secondTestBook.setAuthor("Another Test Author");

        bookList = Arrays.asList(firstTestBook, secondTestBook);

        testUser = new User();
        testUser.setId(1);
        testUser.setUserName("Test User");
    }

    @Test
    void getAll_ReturnsAllBooks() {
        when(bookRepository.findAll()).thenReturn(bookList);

        Iterable<Book> result = bookService.getAll();

        assertEquals(bookList, result);
    }

    @Test
    void get_WithValidId_ReturnsBook() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(firstTestBook));

        Book result = bookService.get(1);

        assertEquals(firstTestBook, result);
    }

    @Test
    void get_WithInvalidId_ThrowsException() {
        when(bookRepository.findById(999)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            bookService.get(999);
        });

        assertEquals("Book not found with id: 999", exception.getMessage());
        verify(bookRepository).findById(999);
    }

    @Test
    void getBooksByUserId_WhenUserExists_ReturnsBooks() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.findByBorrowerId(1)).thenReturn(Collections.singletonList(firstTestBook));

        List<Book> result = bookService.getBooksByUserId(1);

        assertEquals(1, result.size());
        assertEquals(firstTestBook, result.getFirst());
    }

    @Test
    void getBooksByUserId_WhenUserDoesNotExist_ThrowsException() {
        when(userRepository.existsById(999)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> {
            bookService.getBooksByUserId(999);
        }, "User not found with id: 999");
    }

    @Test
    void searchBooks_WithTitleAndAuthor_ReturnsMatchingBooks() {
        when(bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase("Test Book", "Test Author"))
                .thenReturn(Collections.singletonList(firstTestBook));

        List<Book> result = bookService.searchBooks("Test Book", "Test Author");

        assertEquals(1, result.size());
        assertEquals(firstTestBook, result.getFirst());
    }

    @Test
    void searchBooks_WithTitle_ReturnsMatchingBooks() {
        when(bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase("Test Book", ""))
                .thenReturn(Arrays.asList(firstTestBook, secondTestBook));

        List<Book> result = bookService.searchBooks("Test Book", null);

        assertEquals(2, result.size());
        assertTrue(result.contains(firstTestBook));
        assertTrue(result.contains(secondTestBook));
        verify(bookRepository).findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase("Test Book", "");
    }

    @Test
    void searchBooks_WithAuthor_ReturnsMatchingBooks() {
        when(bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase("", "Test Author"))
                .thenReturn(Collections.singletonList(firstTestBook));

        List<Book> result = bookService.searchBooks(null, "Test Author");

        assertEquals(1, result.size());
        assertEquals(firstTestBook, result.getFirst());
        verify(bookRepository).findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase("", "Test Author");
    }

    @Test
    void searchBooks_NoParameters_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.searchBooks(null, null);
        });

        assertEquals("At least one search parameter (title or author) must be provided", exception.getMessage());
        verify(bookRepository, never()).findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(any(), any());
    }

    @Test
    void create_SavesBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(firstTestBook);

        Book result = bookService.create(firstTestBook);

        assertEquals(firstTestBook, result);
        verify(bookRepository, times(1)).save(firstTestBook);
    }

    @Test
    void create_WithExistingId_ThrowsException() {
        Book bookWithExistingId = new Book();
        bookWithExistingId.setId(1);
        bookWithExistingId.setTitle("Test Book");
        bookWithExistingId.setAuthor("Test Author");

        // Mock that the ID already exists in the repository
        when(bookRepository.existsById(1)).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookService.create(bookWithExistingId);
        });

        assertEquals("Book with ID 1 already exists", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void create_WithZeroId_SavesBook() {
        Book bookWithZeroId = new Book();
        bookWithZeroId.setId(0);
        bookWithZeroId.setTitle("Test Book");
        bookWithZeroId.setAuthor("Test Author");

        when(bookRepository.save(any(Book.class))).thenReturn(bookWithZeroId);

        Book result = bookService.create(bookWithZeroId);

        assertEquals(bookWithZeroId, result);
        verify(bookRepository, never()).existsById(anyInt());
        verify(bookRepository, times(1)).save(bookWithZeroId);
    }

    @Test
    void create_WithNullId_SavesBook() {
        Book bookWithNullId = new Book();
        bookWithNullId.setId(null);
        bookWithNullId.setTitle("Test Book");
        bookWithNullId.setAuthor("Test Author");

        when(bookRepository.save(any(Book.class))).thenReturn(bookWithNullId);

        Book result = bookService.create(bookWithNullId);

        assertEquals(bookWithNullId, result);
        verify(bookRepository, never()).existsById(anyInt());
        verify(bookRepository, times(1)).save(bookWithNullId);
    }

    @Test
    void remove_DeletesBook() {
        doNothing().when(bookRepository).deleteById(1);

        bookService.remove(1);

        verify(bookRepository, times(1)).deleteById(1);
    }

    @Test
    void remove_WithInvalidId_ThrowsException() {
        doThrow(new EmptyResultDataAccessException("", 1)).when(bookRepository).deleteById(999);

        assertThrows(EmptyResultDataAccessException.class, () -> {
            bookService.remove(999);
        });

        verify(bookRepository, times(1)).deleteById(999);
    }

    @Test
    void update_WithExistingBook_UpdatesBook() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(firstTestBook));
        when(bookRepository.save(any(Book.class))).thenReturn(firstTestBook);

        Book result = bookService.update(firstTestBook);

        assertEquals(firstTestBook, result);
        verify(bookRepository, times(1)).findById(1);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void update_WithNonExistingBook_ThrowsException() {
        when(bookRepository.findById(firstTestBook.getId())).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            bookService.update(firstTestBook);
        });

        assertTrue(exception.getMessage().contains(String.valueOf(firstTestBook.getId())));
        verify(bookRepository, times(1)).findById(firstTestBook.getId());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void borrowBook_WhenBookAndUserExist_BorrowsBook() {
        Book availableBook = new Book();
        availableBook.setId(1);
        availableBook.setTitle("Available Book");
        availableBook.setAvailable(true);
        availableBook.setBorrowerId(null);

        Book borrowedBook = new Book();
        borrowedBook.setId(1);
        borrowedBook.setTitle("Available Book");
        borrowedBook.setAvailable(false);
        borrowedBook.setBorrowerId(1);

        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.findById(1)).thenReturn(Optional.of(availableBook));
        when(bookRepository.save(any(Book.class))).thenReturn(borrowedBook);

        Book result = bookService.borrowBook(1, 1);

        assertEquals(1, result.getBorrowerId());
        assertFalse(result.isAvailable());
        verify(bookRepository, times(1)).findById(1);
        verify(userRepository, times(1)).existsById(1);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void borrowBook_WhenBookIsAlreadyBorrowed_ThrowsException() {
        Book borrowedBook = new Book();
        borrowedBook.setId(1);
        borrowedBook.setTitle("Borrowed Book");
        borrowedBook.setAvailable(false);
        borrowedBook.setBorrowerId(2);  // Already borrowed by someone else

        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.findById(1)).thenReturn(Optional.of(borrowedBook));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookService.borrowBook(1, 1);
        });

        assertEquals("Book is already borrowed", exception.getMessage());

        verify(bookRepository, times(1)).findById(1);
        verify(userRepository, times(1)).existsById(1);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void borrowBook_WhenUserDoesNotExist_ThrowsException() {
        when(userRepository.existsById(999)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            bookService.borrowBook(1, 999);
        });

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository, times(1)).existsById(999);
        verify(bookRepository, never()).findById(anyInt());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void borrowBook_WhenBookDoesNotExist_ThrowsException() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.findById(999)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            bookService.borrowBook(999, 1);
        });

        assertEquals("Book not found with id: 999", exception.getMessage());
        verify(userRepository, times(1)).existsById(1);
        verify(bookRepository, times(1)).findById(999);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void returnBook_WhenBookIsBorrowed_ReturnsBook() {
        Book borrowedBook = new Book();
        borrowedBook.setId(1);
        borrowedBook.setTitle("Borrowed Book");
        borrowedBook.setAvailable(false);
        borrowedBook.setBorrowerId(1);  // Borrowed

        Book returnedBook = new Book();
        returnedBook.setId(1);
        returnedBook.setTitle("Borrowed Book");
        returnedBook.setAvailable(true);
        returnedBook.setBorrowerId(null);  // Returned

        when(bookRepository.findById(1)).thenReturn(Optional.of(borrowedBook));
        when(bookRepository.save(any(Book.class))).thenReturn(returnedBook);

        Book result = bookService.returnBook(1);

        assertNull(result.getBorrowerId());
        assertTrue(result.isAvailable());
        verify(bookRepository, times(1)).findById(1);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void returnBook_WhenBookIsNotBorrowed_ThrowsException() {
        Book availableBook = new Book();
        availableBook.setId(1);
        availableBook.setTitle("Available Book");
        availableBook.setAvailable(true);
        availableBook.setBorrowerId(null);  // Not borrowed

        when(bookRepository.findById(1)).thenReturn(Optional.of(availableBook));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookService.returnBook(1);
        });

        assertEquals("Book is not currently borrowed", exception.getMessage());
        verify(bookRepository, times(1)).findById(1);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void returnBook_WhenBookDoesNotExist_ThrowsException() {
        when(bookRepository.findById(999)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            bookService.returnBook(999);
        });

        assertEquals("Book not found with id: 999", exception.getMessage());
        verify(bookRepository, times(1)).findById(999);
        verify(bookRepository, never()).save(any(Book.class));
    }
}