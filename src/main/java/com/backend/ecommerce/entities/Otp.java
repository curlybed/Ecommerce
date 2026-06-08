package com.backend.ecommerce.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Otp {

    @Id
    private String id;
    @Indexed
    private String email;
    private String otpCode;
    @Indexed
    private Date expiresAt;
}
