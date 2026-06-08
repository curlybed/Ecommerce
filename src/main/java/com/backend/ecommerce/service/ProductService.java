package com.backend.ecommerce.service;

import com.backend.ecommerce.entities.Product;
import com.backend.ecommerce.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product findByProductId(String productId) {
        return productRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
    }

    @Transactional
    public Product updateProduct(String productId, Product updatedproduct) {
        Product existingProduct = findByProductId(productId);
        existingProduct.setProductName(updatedproduct.getProductName());
        existingProduct.setProductDescription(updatedproduct.getProductDescription());
        existingProduct.setProductPrice(updatedproduct.getProductPrice());
        existingProduct.setProductQuantity(updatedproduct.getProductQuantity());
        existingProduct.setCategory(updatedproduct.getCategory());
        existingProduct.setManufacturer(updatedproduct.getManufacturer());
        return productRepository.save(existingProduct);
    }

    @Transactional
    public void deleteProduct(String productId) {
        Product product = findByProductId(productId);
        productRepository.delete(product);
    }

    @Transactional
    public Product addProduct(Product newproduct) {
        Product newProduct = Product.builder()
                .productName(newproduct.getProductName())
                .productDescription(newproduct.getProductDescription())
                .productPrice(newproduct.getProductPrice())
                .productQuantity(newproduct.getProductQuantity())
                .category(newproduct.getCategory())
                .manufacturer(newproduct.getManufacturer())
                .build();

        return productRepository.save(newProduct);
    }

    @Transactional
    public List<Product> getProductsByCategoryId(String categoryId) {
        return productRepository.findByCategory_CategoryId(categoryId);
    }

    @Transactional
    public List<Product> getProducts() {
        return productRepository.findAll();
    }








}
