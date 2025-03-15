package org.example.spring_addressbookapp.service;

import org.example.spring_addressbookapp.dto.AuthUserDTO;
import org.example.spring_addressbookapp.dto.ContactDTO;
import org.example.spring_addressbookapp.dto.LoginDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface AuthenticationServiceInterface {

    List<AuthUserDTO> getAllUsers();

    String registerUser(AuthUserDTO authUserDTO , String masterKey);

    ResponseEntity<Map<String, String>> loginUser(LoginDTO loginDTO);

    String forgotPassword(String email, String newPassword);

    String resetPassword(String email, String currentPassword, String newPassword);
}
