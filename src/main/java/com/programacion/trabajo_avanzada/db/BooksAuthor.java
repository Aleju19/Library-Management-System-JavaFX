package com.programacion.trabajo_avanzada.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("books_authors")
public class BooksAuthor {

     @Column("books_isbn")
     private String booksIsbn;
     @Column("authors_id")
     private Integer authorsId;

}
