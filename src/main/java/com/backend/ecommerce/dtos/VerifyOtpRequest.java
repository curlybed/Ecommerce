package com.backend.ecommerce.dtos;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String email;
    private String otp;
}
