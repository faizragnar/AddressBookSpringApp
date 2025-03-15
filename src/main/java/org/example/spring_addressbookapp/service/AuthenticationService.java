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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @Override
    public String forgotPassword(String email, String newPassword) {
        Optional<AuthUser> userOptional = authUserRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Sorry! We cannot find the user email: " + email);
        }

        AuthUser user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        authUserRepository.save(user);

        emailService.sendEmail(email, "Password Changed", "Your password has been updated successfully.");

        return "Password has been changed successfully!";
    }

    @Override
    public String resetPassword(String email, String currentPassword, String newPassword) {
        Optional<AuthUser> userOptional = authUserRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with email: " + email);
        }

        AuthUser user = userOptional.get();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        authUserRepository.save(user);

        emailService.sendEmail(email, "Password Reset", "Your password has been updated successfully.");

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
