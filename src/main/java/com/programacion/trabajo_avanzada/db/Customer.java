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
@Table("customers")
public class Customer {
    @Id
    private Long id; // PK
    private String name;
    private String email;
    private Integer version;
}