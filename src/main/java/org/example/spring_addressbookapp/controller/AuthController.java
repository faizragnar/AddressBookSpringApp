package org.example.spring_addressbookapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.spring_addressbookapp.dto.AuthUserDTO;
import org.example.spring_addressbookapp.dto.ContactDTO;
import org.example.spring_addressbookapp.dto.LoginDTO;
import org.example.spring_addressbookapp.service.JwtUserDetailsService;
import org.example.spring_addressbookapp.utility.JwtUtility;
import org.example.spring_addressbookapp.service.AuthenticationServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication API", description = "Endpoints for user authentication")
public class AuthController {


    @Autowired
    AuthenticationServiceInterface authenticationServiceInterface;

    @Value("${app.master.key}")
    String masterKey;

    @GetMapping("/admin/all")
    public List<AuthUserDTO> getAllUsers() {
        return authenticationServiceInterface.getAllUsers();
    }
    // 🔹 Register a New User
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @Valid @RequestBody AuthUserDTO authUserDTO,
            @RequestHeader(value = "masterkey", required = false) String receivedMasterKey
    ) {
        return ResponseEntity.ok(authenticationServiceInterface.registerUser(authUserDTO, receivedMasterKey));
    }

    // 🔹 Login Functionality
    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticates a user and returns a JWT token")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        return authenticationServiceInterface.loginUser(loginDTO);
    }

    // 🔹 Forgot Password
    @PutMapping("/forgot/{email}")
    @Operation(summary = "Forgot Password", description = "Allows users to reset their password if forgotten")
    public Map<String, String> forgotPassword(@PathVariable String email, @RequestBody Map<String, String> requestBody) {
        String responseMessage = authenticationServiceInterface.forgotPassword(email, requestBody.get("password"));
        return Map.of("message", responseMessage);
    }

    // 🔹 Reset Password
    @PutMapping("/resetPassword/{email}")
    @Operation(summary = "Reset Password", description = "Enables users to securely update their password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @PathVariable String email, @RequestBody Map<String, String> passwordRequest) {

        String currentPassword = passwordRequest.get("currentPassword");
        String newPassword = passwordRequest.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Both current and new passwords are required!"));
        }

        String responseMessage = authenticationServiceInterface.resetPassword(email, currentPassword, newPassword);
        return ResponseEntity.ok(Map.of("message", responseMessage));
    }
}
