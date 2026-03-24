package com.programacion.trabajo_avanzada.repositories;

import com.programacion.trabajo_avanzada.db.Author;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuthorRepository extends CrudRepository<Author, Long> {

}
