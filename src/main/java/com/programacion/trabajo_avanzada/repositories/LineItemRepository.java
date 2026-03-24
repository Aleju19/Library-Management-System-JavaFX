package com.programacion.trabajo_avanzada.repositories;

import com.programacion.trabajo_avanzada.db.LineItem;
import org.springframework.data.repository.CrudRepository;


public interface LineItemRepository extends CrudRepository<LineItem, Integer> {

}