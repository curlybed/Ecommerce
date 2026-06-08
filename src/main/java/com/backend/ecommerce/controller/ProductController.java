package com.backend.ecommerce.controller;

import com.backend.ecommerce.entities.Product;
import com.backend.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public Product addProduct(@RequestBody Product product){
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
    public void deleteProduct(@PathVariable String productId){
        productService.deleteProduct(productId);
    }


    @PutMapping("/update/{productId}")
    public Product updateProduct(@PathVariable String productId, @RequestBody Product product){
        return productService.updateProduct(productId, product);
    }

}
