package org.example.spring_addressbookapp.service;

import org.example.spring_addressbookapp.dto.ContactDTO;

import java.util.List;

public interface ContactService {
    List<ContactDTO> getAllContacts();
    ContactDTO getContactById(int id);
    ContactDTO addContact(ContactDTO contactDTO);
    ContactDTO updateContact(int id, ContactDTO updatedContactDTO);
    void deleteContact(int id);
}
