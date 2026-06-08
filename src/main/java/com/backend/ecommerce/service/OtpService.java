package com.backend.ecommerce.service;

import com.backend.ecommerce.entities.Otp;
import com.backend.ecommerce.repositories.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Value("${otp.expiration-minutes}")
    private int expirationMinutes;

    @Transactional
    public String generateOtp(String email){
        otpRepository.deleteByEmail(email);

        String otpCode = String.format("%06d", new Random().nextInt(999999));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, expirationMinutes);

        Otp otpEntity = Otp.builder()
                .email(email)
                .otpCode(otpCode)
                .expiresAt(calendar.getTime())
                .build();

        otpRepository.save(otpEntity);


        return otpCode;
    }

    @Transactional
    public boolean verifyOtp(String email, String otpCode){
        Optional<Otp> otp = otpRepository.findByEmailAndOtpCode(email, otpCode);
        if(otp.isPresent() && otp.get().getExpiresAt().after(new Date())){
            otpRepository.deleteByEmail(email);
            return true;
        }
        return false;
    }
}


