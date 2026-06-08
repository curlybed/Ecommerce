package com.backend.ecommerce.controller;

import com.backend.ecommerce.entities.Product;
import com.backend.ecommerce.entities.User;
import com.backend.ecommerce.enums.Role;
import com.backend.ecommerce.repositories.UserRepository;
import com.backend.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    private void verifyAdmin(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing User ID");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        
        if (user.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can perform this action");
        }
    }

    @PostMapping("/add")
    public Product addProduct(@RequestHeader("X-User-Id") String userId, @RequestBody Product product){
        verifyAdmin(userId);
        return productService.addProduct(product);
    }

    @GetMapping("/view")
    public List<Product> getProducts(){
        return productService.getProducts();
    }

    @GetMapping("/view/category/{categoryId}")
    public List<Product> findByCategoryId(@PathVariable String categoryId){
        return productService.getProductsByCategoryId(categoryId);
    }

    @DeleteMapping("/delete/{productId}")
    public void deleteProduct(@RequestHeader("X-User-Id") String userId, @PathVariable String productId){
        verifyAdmin(userId);
        productService.deleteProduct(productId);
    }

    @PutMapping("/update/{productId}")
    public Product updateProduct(@RequestHeader("X-User-Id") String userId, @PathVariable String productId, @RequestBody Product product){
        verifyAdmin(userId);
        return productService.updateProduct(productId, product);
    }

}
