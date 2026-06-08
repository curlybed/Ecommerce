package com.backend.ecommerce.service;

import com.backend.ecommerce.entities.Order;
import com.backend.ecommerce.repositories.OrderRepository;
import com.backend.ecommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
public class OrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public Order getOrderById(String orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    @Transactional
    public Order createOrder(Order order) {
        String newOrderId = UUID.randomUUID().toString();
        Order newOrder = Order.builder()
                .orderId(newOrderId)
                .orderDate(LocalDateTime.now())
                .orderStatus("PENDING")
                .build();

        return orderRepository.save(newOrder);
    }


    @Transactional
    public Order deleteOrder(String orderId) {
        Order newwOrder = orderRepository.findByOrderId(orderId);
        orderRepository.delete(newwOrder);
        return newwOrder;
    }
}
