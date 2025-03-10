package org.example.spring_addressbookapp.service;

import org.example.spring_addressbookapp.dto.AuthUserDTO;
import org.example.spring_addressbookapp.dto.LoginDTO;

import java.util.Map;

public interface AuthenticationServiceInterface {

    String registerUser(AuthUserDTO authUserDTO);

    Map<String, String> loginUser(LoginDTO loginDTO);

    String forgotPassword(String email, String newPassword);

    String resetPassword(String email, String currentPassword, String newPassword);
}