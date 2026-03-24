package com.programacion.trabajo_avanzada.repositories;

import com.programacion.trabajo_avanzada.db.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
}