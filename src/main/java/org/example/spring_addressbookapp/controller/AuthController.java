package org.example.spring_addressbookapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.spring_addressbookapp.dto.AuthUserDTO;
import org.example.spring_addressbookapp.dto.LoginDTO;
import org.example.spring_addressbookapp.service.AuthenticationServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication API", description = "Endpoints for user authentication")
public class AuthController {

    @Autowired
    private AuthenticationServiceInterface authenticationServiceInterface;

    @Value("${app.master.key}")
    private String masterKey;

    @GetMapping("/admin/all")
    public List<AuthUserDTO> getAllUsers() {
        return authenticationServiceInterface.getAllUsers();
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @Valid @RequestBody AuthUserDTO authUserDTO,
            @RequestHeader(value = "masterkey", required = false) String receivedMasterKey
    ) {
        return ResponseEntity.ok(authenticationServiceInterface.registerUser(authUserDTO, receivedMasterKey));
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticates a user and returns a JWT token")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        return authenticationServiceInterface.loginUser(loginDTO);
    }

    // 🔹 Forgot Password - Generate Token
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        return ResponseEntity.ok(authenticationServiceInterface.generateResetToken(email));
    }

    // 🔹 Reset Password - Verify Token
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token, @RequestParam String newPassword) {
        return ResponseEntity.ok(authenticationServiceInterface.resetPassword(token, newPassword));
    }
}
