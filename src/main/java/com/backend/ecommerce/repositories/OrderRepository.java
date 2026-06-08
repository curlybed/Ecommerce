package com.backend.ecommerce.repositories;

import com.backend.ecommerce.entities.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order,String> {

    Order findByOrderId(String orderId);
}
