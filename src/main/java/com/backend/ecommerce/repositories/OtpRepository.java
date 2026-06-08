package com.backend.ecommerce.repositories;

import com.backend.ecommerce.entities.Otp;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OtpRepository extends MongoRepository<Otp,String> {


    Optional<Otp> findByEmailAndOtpCode(String email, String otpCode);
    void deleteByEmail(String email);
}
