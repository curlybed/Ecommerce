package com.backend.ecommerce.repositories;

import com.backend.ecommerce.entities.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PaymentRepository extends MongoRepository<Payment, String> {

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
    Optional<Payment> findByPaymentId(String paymentId);
}
