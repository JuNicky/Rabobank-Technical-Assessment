package com.nicky.rabobank.technical.assessment.repository;

import com.nicky.rabobank.technical.assessment.model.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends CrudRepository<Book, Integer> {
    // All the functions below uses Spring Data's naming conventions to
    // automatically generate SQL queries.

    /**
     * Retrieves all books borrowed by a specific user.
     *
     * @param borrowerId the ID of the user who borrowed the books
     * @return a list of books borrowed by the specified user
     */
    List<Book> findByBorrowerId(Integer borrowerId);

    /**
     * Finds books matching both the specified title and author patterns.
     * The search is case-insensitive and works with partial matches.
     *
     * @param title a substring of the book title to search for
     * @param author a substring of the book author to search for
     * @return a list of books matching both the title and author search
     *         criteria
     */
    List<Book> findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(
            String title, String author
    );
}
