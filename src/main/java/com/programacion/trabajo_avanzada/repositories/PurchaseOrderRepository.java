package com.programacion.trabajo_avanzada.repositories;

import com.programacion.trabajo_avanzada.db.PurchaseOrder;
import org.springframework.data.repository.CrudRepository;

public interface PurchaseOrderRepository extends CrudRepository<PurchaseOrder, Long> {
}