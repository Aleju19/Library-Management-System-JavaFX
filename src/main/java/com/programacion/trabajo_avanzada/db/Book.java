package com.programacion.trabajo_avanzada.db;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("books")
public class Book {
    @Id
    private String isbn;
    private String title;
    private Double price;
    private Integer version;
}
