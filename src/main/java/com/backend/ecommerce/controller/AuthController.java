package com.backend.ecommerce.controller;
import com.backend.ecommerce.dtos.ResendOtpRequest;
import com.backend.ecommerce.dtos.VerifyOtpRequest;
import com.backend.ecommerce.dtos.LoginRequest;
import com.backend.ecommerce.entities.User;
import com.backend.ecommerce.dtos.RegisterRequest;
import com.backend.ecommerce.repositories.UserRepository;
import com.backend.ecommerce.service.EmailService;
import com.backend.ecommerce.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

   @Autowired
    private UserRepository userRepository;

   @Autowired
    private PasswordEncoder passwordEncoder;

   @Autowired
    private EmailService emailService;

   @Autowired
    private OtpService otpService;

   @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest){
       Optional<User> existing = userRepository.findByEmail(registerRequest.getEmail());

       if(existing.isPresent() && existing.get().isEmailVerified()) {
           return ResponseEntity.status(HttpStatus.CONFLICT)
                   .body(Map.of("message", "User with this email already exists"));
       }


       existing.ifPresent(userRepository::delete);

       User user = User.builder()
               .email(registerRequest.getEmail())
               .password(passwordEncoder.encode(registerRequest.getPassword()))
               .name(registerRequest.getName())
               .emailVerified(false)
               .build();

       userRepository.save(user);

       String otp = otpService.generateOtp(registerRequest.getEmail());
       emailService.sendOtpEmail(registerRequest.getEmail(), otp);

       return ResponseEntity.ok(Map.of("message", "User registered successfully. Please check your email for OTP."));



   }

   @PostMapping("/verify-otp")
   public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request){

       boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtp());

       if(!isValid){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                   .body(Map.of("message", "Invalid OTP"));
       }

       User user = userRepository.findByEmail(request.getEmail())
               .orElseThrow(() -> new RuntimeException("User not found"));

       user.setEmailVerified(true);
       userRepository.save(user);

       return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
   }

   @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody ResendOtpRequest request){
        String email = request.getEmail();
        User user = userRepository.findByEmail(email).orElse(null);
       if(user == null){
           return ResponseEntity.status(HttpStatus.NOT_FOUND)
           .body(Map.of("message", "No account found"));
       }
       if(user.isEmailVerified()){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST)
           .body(Map.of("message", "Email already verified"));
       }
       String otp = otpService.generateOtp(email);
       emailService.sendOtpEmail(email, otp);
       return ResponseEntity.ok(Map.of("message", "OTP resent successfully"));
    }

    @PostMapping("/login")
     public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElse(null);
            if(user == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "User not found"));
            }
            if(!user.isEmailVerified()){
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "verify the Email"));
            }
            if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid password"));
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "userId", user.getUserId(),
                    "name", user.getName(),
                    "email", user.getEmail()
            ));
     }
}
