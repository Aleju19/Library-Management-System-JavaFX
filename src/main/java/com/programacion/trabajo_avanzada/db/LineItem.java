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
@Table("line_items")
public class LineItem {
    @Id
    private Integer id; // PK
    private Integer orderId; // FK hacia purchaseorder.id
    private Integer quantity;
    private String isbn; // FK hacia book.isbn

}