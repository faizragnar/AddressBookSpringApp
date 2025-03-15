package org.example.spring_addressbookapp.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.example.spring_addressbookapp.dto.ContactDTO;
import org.example.spring_addressbookapp.service.ContactService;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/all")
    public List<ContactDTO> getAllContacts() {
        return contactService.getAllContacts();
    }

    @GetMapping("/get/{id}")
    public ContactDTO getContactById(@PathVariable int id) {
        return contactService.getContactById(id);
    }

    @PostMapping("/add")
    public ContactDTO addContact(@RequestBody @Valid ContactDTO contactDTO) {
        return contactService.addContact(contactDTO);
    }

    @PutMapping("/update/{id}")
    public ContactDTO updateContact(@PathVariable int id,@Valid @RequestBody ContactDTO updatedContact) {
        return contactService.updateContact(id, updatedContact);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteContact(@PathVariable int id) {
        contactService.deleteContact(id);
    }
}