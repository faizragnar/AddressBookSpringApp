package org.example.spring_addressbookapp.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class ContactDTO {
    private int id;
    @NotBlank(message = "Name is required")
    @Size(max = 10, message = "Name must not exceed 10 characters")
    @Pattern(regexp = "^[A-Z][a-zA-Z\\s]*$", message = "Name can only contain letters and first letter should be capital")
    private String name;
    @NotBlank
    private String phone;
    @NotBlank
    private String email;
    @NotBlank
    private String address;


}