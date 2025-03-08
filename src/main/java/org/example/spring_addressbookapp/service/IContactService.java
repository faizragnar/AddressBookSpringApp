package org.example.spring_addressbookapp.service;

import lombok.extern.slf4j.Slf4j;
import org.example.spring_addressbookapp.dto.ContactDTO;
import org.example.spring_addressbookapp.model.Contact;
import org.example.spring_addressbookapp.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IContactService implements ContactService {

    @Autowired
    ContactRepository contactRepository;

    @Override
    public List<ContactDTO> getAllContacts() {
        log.info("Fetching all contacts...");
        return contactRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public ContactDTO getContactById(int id) {
        log.info("Fetching contact with ID: {}", id);
        return contactRepository.findById(id).map(this::convertToDTO).orElse(null);
    }

    @Override
    public ContactDTO addContact(ContactDTO contactDTO) {
        try {
            log.info("Adding new contact: {}", contactDTO.getName());
            Contact contact = convertToEntity(contactDTO);
            Contact savedContact = contactRepository.save(contact);
            log.info("Contact added with ID: {}", savedContact.getId());
            return convertToDTO(savedContact);
        } catch (Exception e) {
            log.error("Error adding contact: {}", e.getMessage());
            throw new RuntimeException("Failed to add contact.");
        }
    }

    @Override
    public ContactDTO updateContact(int id, ContactDTO updatedContactDTO) {
        try {
            log.info("Updating contact with ID: {}", id);
            Contact existingContact = contactRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Contact not found with ID: " + id));

            existingContact.setName(updatedContactDTO.getName());
            existingContact.setPhone(updatedContactDTO.getPhone());
            existingContact.setEmail(updatedContactDTO.getEmail());
            existingContact.setAddress(updatedContactDTO.getAddress());

            Contact updatedContact = contactRepository.save(existingContact);
            log.info("Updated contact with ID: {}", updatedContact.getId());
            return convertToDTO(updatedContact);
        } catch (Exception e) {
            log.error("Error updating contact: {}", e.getMessage());
            throw new RuntimeException("Failed to update contact.");
        }
    }

    @Override
    public void deleteContact(int id) {
        log.warn("Deleting contact with ID: {}", id);
        contactRepository.deleteById(id);
        log.info("Deleted contact with ID: {}", id);
    }

    private ContactDTO convertToDTO(Contact contact) {
        return new ContactDTO(contact.getId(), contact.getName(), contact.getPhone(), contact.getEmail(), contact.getAddress());
    }

    private Contact convertToEntity(ContactDTO contactDTO) {
        return new Contact(contactDTO.getId(), contactDTO.getName(), contactDTO.getPhone(), contactDTO.getEmail(), contactDTO.getAddress());
    }
}
