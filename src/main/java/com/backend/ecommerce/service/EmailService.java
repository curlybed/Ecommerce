package com.backend.ecommerce.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
     private static final Logger log = LoggerFactory.getLogger(EmailService.class);

     @Autowired(required = false)
     private JavaMailSender mailSender;

     public void sendOtpEmail(String toEmail, String otp) {
         if (mailSender == null) {
             log.warn("Email service not configured, email not sent to {}", toEmail);
             return;
         }
         try{
             SimpleMailMessage message = new SimpleMailMessage();
             message.setTo(toEmail);
             message.setSubject("Your otp Code:");
             message.setText(otp);
             mailSender.send(message);
         } catch (Exception e) {
             log.error("Failed to send email to {}", toEmail, e);
         }

     }
}
