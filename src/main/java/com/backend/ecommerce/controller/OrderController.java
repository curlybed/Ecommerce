package com.backend.ecommerce.controller;

import com.backend.ecommerce.entities.Order;
import com.backend.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Order createOrder(@RequestBody Order order){
        return orderService.createOrder(order);
    }

    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable String orderId){
        return orderService.getOrderById(orderId);
    }

    @DeleteMapping("/{orderId}")
    public Order deleteOrder(@PathVariable String orderId){
        return orderService.deleteOrder(orderId);
    }

}
