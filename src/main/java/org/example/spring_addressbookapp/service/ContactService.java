package org.example.spring_addressbookapp.service;

import org.example.spring_addressbookapp.dto.ContactDTO;
import org.example.spring_addressbookapp.model.Contact;
import org.example.spring_addressbookapp.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactService implements IContactService {

    ContactRepository contactRepository;

    @Autowired
    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public List<ContactDTO> getAllContacts() {
        return contactRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ContactDTO getContactById(int id) {
        return contactRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Contact not found!"));
    }

    @Override
    public ContactDTO addContact(ContactDTO contactDTO) {
        Contact contact = convertToEntity(contactDTO);
        Contact savedContact = contactRepository.save(contact);
        return convertToDTO(savedContact);
    }

    @Override
    public ContactDTO updateContact(int id, ContactDTO updatedContactDTO) {
        Contact existingContact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found!"));

        existingContact.setName(updatedContactDTO.getName());
        existingContact.setPhone(updatedContactDTO.getPhone());
        existingContact.setEmail(updatedContactDTO.getEmail());
        existingContact.setAddress(updatedContactDTO.getAddress());

        Contact updatedContact = contactRepository.save(existingContact);
        return convertToDTO(updatedContact);
    }

    @Override
    public void deleteContact(int id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found!"));
        contactRepository.delete(contact);
    }

    private ContactDTO convertToDTO(Contact contact) {
        return new ContactDTO(contact.getId(), contact.getName(), contact.getPhone(), contact.getEmail(), contact.getAddress());
    }

    // ✅ Fixed: Updated to include correct constructor
    private Contact convertToEntity(ContactDTO contactDTO) {
        return new Contact(contactDTO.getId(), contactDTO.getName(), contactDTO.getPhone(), contactDTO.getEmail(), contactDTO.getAddress());
    }
}

