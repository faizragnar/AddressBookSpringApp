package org.example.spring_addressbookapp.service;

import org.example.spring_addressbookapp.dto.AuthUserDTO;
import org.example.spring_addressbookapp.dto.ContactDTO;
import org.example.spring_addressbookapp.dto.LoginDTO;
import org.example.spring_addressbookapp.model.AuthUser;
import org.example.spring_addressbookapp.repository.AuthUserRepository;
import org.example.spring_addressbookapp.utility.JwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthenticationService implements AuthenticationServiceInterface {

    @Autowired
    AuthUserRepository authUserRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    JwtUtility jwtUtility;

    @Autowired
    JwtUserDetailsService userDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${app.master.key}")
    private String masterKey;


    @Override
    public String registerUser(AuthUserDTO authUserDTO, String receivedMasterKey) {
        if (authUserRepository.findByEmail(authUserDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered!");
        }

        AuthUser newUser = new AuthUser();
        newUser.setFirstName(authUserDTO.getFirstName());
        newUser.setLastName(authUserDTO.getLastName());
        newUser.setEmail(authUserDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(authUserDTO.getPassword()));

        // 🔹 Secure Role Assignment
        String role = authUserDTO.getRole().toUpperCase();
        if ("ADMIN".equalsIgnoreCase(role)) {
            if (receivedMasterKey == null || !receivedMasterKey.equals(masterKey)) {
                throw new RuntimeException("Unauthorized: Valid Master Key required to create ADMIN role!");
            }
        } else if (!role.equals("USER")) {
            role = "USER";  // Default role if invalid or empty
        }

        newUser.setRole(role);
        authUserRepository.save(newUser);

        if (emailService != null) {
            emailService.sendEmail(authUserDTO.getEmail(), "Welcome to Greeting App!", "Thank you for registering as " + role + "!");
        } else {
            throw new RuntimeException("Email service is not initialized properly.");
        }

        return "User registered successfully as " + role + "!";
    }
    @Override
    public List<AuthUserDTO> getAllUsers() {
        try {
            return authUserRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve user info.");
        }
    }

    @Override
    public ResponseEntity<Map<String, String>> loginUser(LoginDTO loginDTO) {
        Optional<AuthUser> userOptional = authUserRepository.findByEmail(loginDTO.getEmail());

        if (userOptional.isPresent() && passwordEncoder.matches(loginDTO.getPassword(), userOptional.get().getPassword())) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());  // 🔹 Load UserDetails
            String token = jwtUtility.generateToken(userDetails);  // 🔹 Generate JWT with UserDetails

            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful!");
            response.put("token", token);

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid email or password"));
    }

    public String generateResetToken(String email) {
        Optional<AuthUser> userOptional = authUserRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with email: " + email);
        }

        AuthUser user = userOptional.get();
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(15)); // Token expires in 15 minutes
        authUserRepository.save(user);

        // Send email with reset token link
        String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + resetToken;
        emailService.sendEmail(email, "Password Reset Request",
                "Click the following link to reset your password: " + resetLink);

        return "Password reset email sent successfully!";
    }

    public String resetPassword(String token, String newPassword) {
        Optional<AuthUser> userOptional = authUserRepository.findByResetToken(token);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Invalid or expired reset token.");
        }

        AuthUser user = userOptional.get();

        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired. Please request a new reset link.");
        }

        // 🔒 Encrypting the New Password Before Saving
        user.setPassword(passwordEncoder.encode(newPassword));

        user.setResetToken(null);
        user.setTokenExpiry(null);
        authUserRepository.save(user);

        return "Password reset successfully!";
    }
    private AuthUserDTO convertToDTO(AuthUser authUser) {
        return new AuthUserDTO(
                authUser.getFirstName(),
                authUser.getLastName(),
                authUser.getEmail(),
                authUser.getPassword(),
                authUser.getRole()
        );
    }

}
