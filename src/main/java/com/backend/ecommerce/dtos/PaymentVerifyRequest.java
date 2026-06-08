package com.backend.ecommerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVerifyRequest {

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

}
