package org.example.spring_addressbookapp.controller;

import org.example.spring_addressbookapp.model.Contact;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final List<Contact> contactList = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    @GetMapping("/all")
    public List<Contact> getAllContacts() {
        return new ArrayList<>(contactList);
    }

    @GetMapping("/get/{id}")
    public Contact getContactById(@PathVariable int id) {
        return contactList.stream()
                .filter(contact -> contact.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Contact not found!"));
    }

    @PostMapping("/add")
    public Contact addContact(@RequestBody Contact contact) {
        contact.setId(idCounter.getAndIncrement()); // Assign unique ID
        contactList.add(contact);
        return contact;
    }

    @PutMapping("/update/{id}")
    public Contact updateContact(@PathVariable int id, @RequestBody Contact updatedContact) {
        Optional<Contact> existingContactOpt = contactList.stream()
                .filter(contact -> contact.getId() == id)
                .findFirst();

        if (existingContactOpt.isPresent()) {
            Contact existingContact = existingContactOpt.get();
            existingContact.setName(updatedContact.getName());
            existingContact.setPhone(updatedContact.getPhone());
            existingContact.setEmail(updatedContact.getEmail());
            existingContact.setAddress(updatedContact.getAddress());
            return existingContact;
        } else {
            throw new RuntimeException("Contact not found!");
        }
    }

    @DeleteMapping("/delete/{id}")
    public void deleteContact(@PathVariable int id) {
        contactList.removeIf(contact -> contact.getId() == id);
    }
}
