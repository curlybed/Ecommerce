package com.backend.ecommerce.repositories;

import com.backend.ecommerce.entities.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product,String> {
    Optional<Product> findByProductId(String productId);

    List<Product> findByCategory_CategoryId(String categoryId);

}
