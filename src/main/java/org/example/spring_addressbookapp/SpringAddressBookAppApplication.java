package org.example.spring_addressbookapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("org.example.spring_addressbookapp.repository")
public class SpringAddressBookAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAddressBookAppApplication.class, args);
    }

}
