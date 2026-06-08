package com.backend.ecommerce.service;

import com.backend.ecommerce.dtos.PaymentRequest;
import com.backend.ecommerce.dtos.PaymentVerifyRequest;
import com.backend.ecommerce.entities.Order;
import com.backend.ecommerce.entities.Payment;
import com.backend.ecommerce.entities.User;
import com.backend.ecommerce.repositories.OrderRepository;
import com.backend.ecommerce.repositories.PaymentRepository;
import com.backend.ecommerce.repositories.UserRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Payment createPayment(PaymentRequest request) throws RazorpayException {

        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findByOrderId(request.getOrderId());
        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int)(request.getAmount() * 100));
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", order.getOrderId());

        com.razorpay.Order razorpayOrder = razorpay.orders.create(orderRequest);

        Payment payment = Payment.builder()
                .razorpayOrderId(razorpayOrder.get("id"))
                .amount(request.getAmount())
                .currency("INR")
                .status("CREATED")
                .order(order)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment verifyPayment(PaymentVerifyRequest request) {

        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        String data = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();
        boolean isValid = verifySignature(data, request.getRazorpaySignature(), razorpayKeySecret);

        if (isValid) {
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setRazorpaySignature(request.getRazorpaySignature());
            payment.setStatus("PAID");
            payment.setPaidAt(LocalDateTime.now());

            Order order = payment.getOrder();
            order.setOrderStatus("CONFIRMED");
            orderRepository.save(order);
        } else {
            payment.setStatus("FAILED");
        }

        return paymentRepository.save(payment);
    }

    private boolean verifySignature(String data, String signature, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes());
            String generatedSignature = HexFormat.of().formatHex(hash);
            return generatedSignature.equals(signature);
        } catch (Exception e) {
            throw new RuntimeException("Error verifying signature", e);
        }
    }
}
