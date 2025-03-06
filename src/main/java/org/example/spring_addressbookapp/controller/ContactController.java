package org.example.spring_addressbookapp.controller;

import org.example.spring_addressbookapp.repository.ContactRepository;
import org.example.spring_addressbookapp.model.Contact;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.example.spring_addressbookapp.dto.ContactDTO;
@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final ContactRepository contactRepository;

    public ContactController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Contact>> getAllContacts() {
        return ResponseEntity.ok(contactRepository.findAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable int id) {
        return contactRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<Contact> addContact(@RequestBody ContactDTO contactDTO) {
        Contact contact = new Contact(contactDTO.getName(), contactDTO.getPhone(), contactDTO.getEmail(), contactDTO.getAddress());
        Contact savedContact = contactRepository.save(contact);
        return ResponseEntity.ok(savedContact);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Contact> updateContact(@PathVariable int id, @RequestBody ContactDTO contactDTO) {
        return contactRepository.findById(id)
                .map(existingContact -> {
                    existingContact.setName(contactDTO.getName());
                    existingContact.setPhone(contactDTO.getPhone());
                    existingContact.setEmail(contactDTO.getEmail());
                    existingContact.setAddress(contactDTO.getAddress());
                    contactRepository.save(existingContact);
                    return ResponseEntity.ok(existingContact);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteContact(@PathVariable int id) {
        return contactRepository.findById(id)
                .map(contact -> {
                    contactRepository.delete(contact);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
