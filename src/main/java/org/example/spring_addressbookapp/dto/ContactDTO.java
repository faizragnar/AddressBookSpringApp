package org.example.spring_addressbookapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactDTO {
    private String name;
    private String phone;
    private String email;
    private String address;

    public ContactDTO(String name, String phone, String email, String address) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }
}