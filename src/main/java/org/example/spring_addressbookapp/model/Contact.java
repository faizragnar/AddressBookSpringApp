package org.example.spring_addressbookapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
@Table(name = "contact")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String phone;
    private String email;
    private String address;

    public Contact() {}

    public Contact (String name, String phone, String email, String address) {

        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }


}