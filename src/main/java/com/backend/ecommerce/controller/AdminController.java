package com.backend.ecommerce.controller;

import com.backend.ecommerce.entities.User;
import com.backend.ecommerce.enums.Role;
import com.backend.ecommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    private void verifyAdmin(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing User ID");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        
        if (user.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can perform this action");
        }
    }

    @GetMapping("/pending-users")
    public List<Map<String, String>> getPendingUsers(@RequestHeader("X-User-Id") String userId) {
        verifyAdmin(userId);
        
        List<User> pendingUsers = userRepository.findAll().stream()
                .filter(u -> !u.isApproved())
                .collect(Collectors.toList());
                
        return pendingUsers.stream().map(u -> Map.of(
                "userId", u.getUserId(),
                "name", u.getName(),
                "email", u.getEmail(),
                "role", u.getRole().name()
        )).collect(Collectors.toList());
    }

    @PostMapping("/approve/{targetUserId}")
    public ResponseEntity<?> approveUser(@RequestHeader("X-User-Id") String adminId, @PathVariable String targetUserId) {
        verifyAdmin(adminId);
        
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User to approve not found"));
                
        targetUser.setApproved(true);
        userRepository.save(targetUser);
        
        return ResponseEntity.ok(Map.of("message", "User approved successfully"));
    }
}
