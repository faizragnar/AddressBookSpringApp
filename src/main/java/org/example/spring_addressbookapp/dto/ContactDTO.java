package org.example.spring_addressbookapp.dto;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
public class ContactDTO {
    private int id;
    private String name;
    private String phone;
    private String email;
    private String address;


}