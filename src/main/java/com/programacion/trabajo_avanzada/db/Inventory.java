package com.programacion.trabajo_avanzada.db;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("inventory")
public class Inventory {

    @Id
    private String isbn; // FK hacia book.isbn
    private Integer sold;
    private Integer supplied;
}