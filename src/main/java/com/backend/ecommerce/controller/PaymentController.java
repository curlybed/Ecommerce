package com.backend.ecommerce.controller;

import com.backend.ecommerce.dtos.PaymentRequest;
import com.backend.ecommerce.dtos.PaymentVerifyRequest;
import com.backend.ecommerce.entities.Payment;
import com.backend.ecommerce.service.PaymentService;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest request) {
        try {
            Payment payment = paymentService.createPayment(request);
            return ResponseEntity.ok(Map.of(
                    "message", "Payment order created",
                    "razorpayOrderId", payment.getRazorpayOrderId(),
                    "amount", payment.getAmount(),
                    "currency", payment.getCurrency(),
                    "paymentId", payment.getPaymentId()
            ));
        } catch (RazorpayException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to create payment: " + e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerifyRequest request) {
        Payment payment = paymentService.verifyPayment(request);

        if ("PAID".equals(payment.getStatus())) {
            return ResponseEntity.ok(Map.of(
                    "message", "Payment verified successfully",
                    "status", payment.getStatus(),
                    "razorpayPaymentId", payment.getRazorpayPaymentId()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", "Payment verification failed",
                            "status", payment.getStatus()
                    ));
        }
    }
}
