package com.programacion.trabajo_avanzada.repositories;

import com.programacion.trabajo_avanzada.db.BooksAuthor;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface BooksAuthorsRepository extends CrudRepository<BooksAuthor, Long> {
    @Modifying
    @Query("INSERT INTO books_authors (books_isbn, authors_id) VALUES(:booksIsbn, :authorsId)")
    void insert(@Param("booksIsbn") String booksIsbn
            , @Param("authorsId") Integer authorsId);

    @Modifying
    @Query("UPDATE books_authors SET authors_id =:authorId WHERE books_isbn =:booksIsbn")
    void update(@Param("booksIsbn") String booksIsbn,
                @Param("authorId") Integer authorId);
    @Modifying
    @Query("DELETE FROM books_authors WHERE books_isbn =:bookIsbn AND authors_id = :authorId")
    void deleteRelacion(@Param("bookIsbn") String bookIsbn
            , @Param("authorId") Integer authorId);
}
