package com.programacion.trabajo_avanzada.repositories;

import com.programacion.trabajo_avanzada.db.Book;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends CrudRepository<Book, String> {

    @Modifying
    @Query("INSERT INTO books (isbn, title, price, version) VALUES (:isbn, :title, :price, :version)")
    void guardarLibro(
            @Param("isbn") String isbn,
            @Param("title") String title,
            @Param("price") Double price,
            @Param("version") Integer version);

}

