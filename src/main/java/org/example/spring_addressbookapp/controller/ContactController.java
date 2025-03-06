package org.example.spring_addressbookapp.controller;

import org.example.spring_addressbookapp.repository.ContactRepository;
import org.example.spring_addressbookapp.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    @Autowired
    ContactRepository contactRepository;

    // GET all contacts
    @GetMapping("/all")
    public ResponseEntity<List<Contact>> getAllContacts() {
        return ResponseEntity.ok(contactRepository.findAll());
    }

    // GET contact by ID
    @GetMapping("/get/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable int id) {
        Optional<Contact> contact = contactRepository.findById(id);
        return contact.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - Add new contact
    @PostMapping("/add")
    public ResponseEntity<Contact> addContact(@RequestBody Contact contact) {
        Contact savedContact = contactRepository.save(contact);
        return ResponseEntity.ok(savedContact);
    }

    // PUT - Update existing contact by ID
    @PutMapping("/update/{id}")
    public ResponseEntity<Contact> updateContact(@PathVariable int id, @RequestBody Contact updatedContact) {
        return contactRepository.findById(id).map(existingContact -> {
            existingContact.setName(updatedContact.getName());
            existingContact.setPhone(updatedContact.getPhone());
            existingContact.setEmail(updatedContact.getEmail());
            existingContact.setAddress(updatedContact.getAddress());
            contactRepository.save(existingContact);
            return ResponseEntity.ok(existingContact);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE - Remove contact by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteContact(@PathVariable int id) {
        if (contactRepository.existsById(id)) {
            contactRepository.deleteById(id);
            return ResponseEntity.ok("Contact deleted successfully.");
        }
        return ResponseEntity.notFound().build();
    }
}
