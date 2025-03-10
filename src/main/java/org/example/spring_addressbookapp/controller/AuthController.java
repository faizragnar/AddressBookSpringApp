package org.example.spring_addressbookapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.spring_addressbookapp.dto.AuthUserDTO;
import org.example.spring_addressbookapp.dto.LoginDTO;
import org.example.spring_addressbookapp.service.AuthenticationServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication API", description = "Endpoints for user authentication")
public class AuthController {

    @Autowired
    AuthenticationServiceInterface authenticationServiceInterface;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Registers a user and sends a welcome email")
    public String registerUser(@Valid @RequestBody AuthUserDTO authUserDTO) {
        return authenticationServiceInterface.registerUser(authUserDTO);
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Allows users to log in and receive a JWT token")
    public Map<String, String> loginUser(@Valid @RequestBody LoginDTO loginDTO) {
        return authenticationServiceInterface.loginUser(loginDTO);
    }
    @PutMapping("/forgot/{email}")
    @Operation(summary = "Forgot Password", description = "Allows users to reset their password if forgotten")
    public Map<String, String> forgotPassword(@PathVariable String email, @RequestBody Map<String, String> requestBody) {
        String responseMessage = authenticationServiceInterface.forgotPassword(email, requestBody.get("password"));
        return Map.of("message", responseMessage);
    }

    @PutMapping("/resetPassword/{email}")
    @Operation(summary = "Reset Password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @PathVariable String email,@RequestBody Map<String, String> passwordRequest) {

        String currentPassword = passwordRequest.get("currentPassword");
        String newPassword = passwordRequest.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Both current and new passwords are required!"));
        }

        String responseMessage = authenticationServiceInterface.resetPassword(email, currentPassword, newPassword);
        return ResponseEntity.ok(Map.of("message", responseMessage));
    }


}
