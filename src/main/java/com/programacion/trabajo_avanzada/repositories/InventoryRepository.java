package com.programacion.trabajo_avanzada.repositories;

import com.programacion.trabajo_avanzada.db.Inventory;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends CrudRepository<Inventory, String> {

    @Modifying
    @Query("INSERT INTO inventory (isbn, sold, supplied) VALUES (:isbn, :sold, :supplied)")
    void guardarInventario(
            @Param("isbn") String isbn,
            @Param("sold") Integer sold,
            @Param("supplied") Integer supplied
    );

}