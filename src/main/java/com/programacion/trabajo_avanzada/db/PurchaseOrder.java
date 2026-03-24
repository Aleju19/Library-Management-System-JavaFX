package com.programacion.trabajo_avanzada.db;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "purchase_orders")
public class PurchaseOrder {
    @Id
    private Long id; // PK
    private Long customerId; // FK hacia customer.id
    private LocalDateTime placedOn;
    private LocalDateTime deliveredOn;
    private Integer status;
    private Integer total;
}