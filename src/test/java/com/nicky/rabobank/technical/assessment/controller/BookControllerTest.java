package com.nicky.rabobank.technical.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicky.rabobank.technical.assessment.model.Book;
import com.nicky.rabobank.technical.assessment.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private Book testBook;
    private List<Book> bookList;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");

        Book secondBook = new Book();
        secondBook.setId(2);
        secondBook.setTitle("Another Book");
        secondBook.setAuthor("Another Author");

        bookList = Arrays.asList(testBook, secondBook);
    }

    @Test
    void getAllBooks_ReturnsAllBooks() throws Exception {
        when(bookService.getAll()).thenReturn(bookList);

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Another Book"));
    }

    @Test
    void getBookById_WhenBookExists_ReturnsBook() throws Exception {
        when(bookService.get(1)).thenReturn(testBook);

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void getBookById_WhenBookDoesNotExist_ReturnsNotFound() throws Exception {
        when(bookService.get(999)).thenReturn(null);

        mockMvc.perform(get("/books/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBooksByUserId_WhenUserExists_ReturnsBooks() throws Exception {
        when(bookService.getBooksByUserId(1)).thenReturn(Collections.singletonList(testBook));

        mockMvc.perform(get("/books/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Book"));
    }

    @Test
    void getBooksByUserId_WhenUserDoesNotExist_ReturnsNotFound() throws Exception {
        when(bookService.getBooksByUserId(999)).thenThrow(new NoSuchElementException("User not found"));

        mockMvc.perform(get("/books/user/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchBooks_WithValidParameters_ReturnsBooks() throws Exception {
        when(bookService.searchBooks("Test", "Author")).thenReturn(Collections.singletonList(testBook));

        mockMvc.perform(get("/books/search")
                        .param("title", "Test")
                        .param("author", "Author"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Book"));
    }

    @Test
    void searchBooks_WithNoParameters_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/books/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchBooks_WithEmptyResults_ReturnsNoContent() throws Exception {
        when(bookService.searchBooks("NonExistent", "Author")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/books/search")
                        .param("title", "NonExistent")
                        .param("author", "Author"))
                .andExpect(status().isNoContent());
    }

    @Test
    void searchBooks_WithOnlyTitle_ReturnsBooks() throws Exception {
        when(bookService.searchBooks("Test", null)).thenReturn(Collections.singletonList(testBook));

        mockMvc.perform(get("/books/search")
                        .param("title", "Test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Book"));
    }

    @Test
    void searchBooks_WithOnlyAuthor_ReturnsBooks() throws Exception {
        when(bookService.searchBooks(null, "Author")).thenReturn(Collections.singletonList(testBook));

        mockMvc.perform(get("/books/search")
                        .param("author", "Author"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Book"));
    }

    @Test
    void searchBooks_WithEmptyParams_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/books/search")
                        .param("title", "")
                        .param("author", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBook_WithValidData_ReturnsCreated() throws Exception {
        when(bookService.create(any(Book.class))).thenReturn(testBook);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void updateBook_WhenBookExists_ReturnsUpdatedBook() throws Exception {
        Book updatedBook = new Book();
        updatedBook.setId(1);
        updatedBook.setTitle("Updated Title");
        updatedBook.setAuthor("Updated Author");

        when(bookService.get(1)).thenReturn(testBook);
        when(bookService.update(any(Book.class))).thenReturn(updatedBook);

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.author").value("Updated Author"));
    }

    @Test
    void updateBook_WhenBookDoesNotExist_ReturnsNotFound() throws Exception {
        when(bookService.get(999)).thenReturn(null);

        mockMvc.perform(put("/books/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBook)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBook_WhenBookExists_ReturnsNoContent() throws Exception {
        when(bookService.get(1)).thenReturn(testBook);
        doNothing().when(bookService).remove(1);

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBook_WhenBookDoesNotExist_ReturnsNotFound() throws Exception {
        when(bookService.get(999)).thenReturn(null);

        mockMvc.perform(delete("/books/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void borrowBook_WhenBookExistsAndAvailable_ReturnsBorrowedBook() throws Exception {
        Book borrowedBook = new Book();
        borrowedBook.setId(1);
        borrowedBook.setTitle("Test Book");
        borrowedBook.setAuthor("Test Author");
        borrowedBook.setBorrowerId(5);

        when(bookService.borrowBook(1, 5)).thenReturn(borrowedBook);

        mockMvc.perform(put("/books/borrow/1/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.borrowerId").value(5));
    }

    @Test
    void borrowBook_WhenBookAlreadyBorrowed_ReturnsBadRequest() throws Exception {
        when(bookService.borrowBook(1, 5)).thenThrow(new IllegalStateException("Book is already borrowed"));

        mockMvc.perform(put("/books/borrow/1/5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void borrowBook_WhenBookDoesNotExist_ReturnsNotFound() throws Exception {
        when(bookService.borrowBook(999, 5)).thenThrow(new NoSuchElementException("Book not found with id: 999"));

        mockMvc.perform(put("/books/borrow/999/5"))
                .andExpect(status().isNotFound());
    }

    @Test
    void borrowBook_WhenUserDoesNotExist_ReturnsNotFound() throws Exception {
        when(bookService.borrowBook(1, 999)).thenThrow(new NoSuchElementException("User not found with id: 999"));

        mockMvc.perform(put("/books/borrow/1/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnBook_WhenBookIsBorrowed_ReturnsReturnedBook() throws Exception {
        Book returnedBook = new Book();
        returnedBook.setId(1);
        returnedBook.setTitle("Test Book");
        returnedBook.setAuthor("Test Author");
        returnedBook.setBorrowerId(null);

        when(bookService.returnBook(1)).thenReturn(returnedBook);

        mockMvc.perform(put("/books/return/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.borrowerId").isEmpty());
    }

    @Test
    void returnBook_WhenBookIsNotBorrowed_ReturnsBadRequest() throws Exception {
        when(bookService.returnBook(1)).thenThrow(new IllegalStateException("Book is not borrowed"));

        mockMvc.perform(put("/books/return/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnBook_WhenBookDoesNotExist_ReturnsNotFound() throws Exception {
        when(bookService.returnBook(999)).thenThrow(new NoSuchElementException("Book not found with id: 999"));

        mockMvc.perform(put("/books/return/999"))
                .andExpect(status().isNotFound());
    }
}