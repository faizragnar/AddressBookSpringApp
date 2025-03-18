package org.example.spring_addressbookapp.controller;

import org.example.spring_addressbookapp.dto.ContactDTO;
import org.example.spring_addressbookapp.exception.ContactNotFoundException;
import org.example.spring_addressbookapp.exception.GlobalExceptionHandler;
import org.example.spring_addressbookapp.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ContactControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ContactService contactService;

    @InjectMocks
    private ContactController contactController;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(contactController)
                .setControllerAdvice(new GlobalExceptionHandler())  // ✅ Add this line
                .build();
    }
    @Test
    void testGetAllContacts() throws Exception {
        List<ContactDTO> contacts = Arrays.asList(
                new ContactDTO(1, "John Doe", "1234567890", "abc@gmail.com", "Agra"),
                new ContactDTO(2, "Jane Doe", "9876543210", "jane@example.com", "Delhi")
        );

        when(contactService.getAllContacts()).thenReturn(contacts);

        mockMvc.perform(get("/api/contacts/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[1].name", is("Jane Doe")));
    }

    @Test
    void testGetContactById() throws Exception {
        ContactDTO contact = new ContactDTO(1, "John Doe", "1234567890", "john@example.com", "Delhi");

        when(contactService.getContactById(1)).thenReturn(contact);

        mockMvc.perform(get("/api/contacts/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));
    }

    @Test
    void testAddContact() throws Exception {
        ContactDTO newContact = new ContactDTO(3, "Alice", "9998887776", "alice@example.com", "Mumbai");

        when(contactService.addContact(any(ContactDTO.class))).thenReturn(newContact);

        mockMvc.perform(post("/api/contacts/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "id": 3,
                        "name": "Alice",
                        "phone": "9998887776",
                        "email": "alice@example.com",
                        "address": "Mumbai"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Alice")))
                .andExpect(jsonPath("$.address", is("Mumbai")));
    }

    @Test
    void testUpdateContact() throws Exception {
        ContactDTO updatedContact = new ContactDTO(1, "John Doe", "7776665554", "john.doe@example.com", "Pune");

        when(contactService.updateContact(eq(1), any(ContactDTO.class))).thenReturn(updatedContact);

        mockMvc.perform(put("/api/contacts/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "id": 1,
                    "name": "John Doe", 
                    "phone": "7776665554",
                    "email": "john.doe@example.com",
                    "address": "Pune"
                }
            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.address", is("Pune")));
    }


    @Test
    void testDeleteContact() throws Exception {
        doNothing().when(contactService).deleteContact(1);

        mockMvc.perform(delete("/api/contacts/delete/1"))
                .andExpect(status().isOk());

        verify(contactService, times(1)).deleteContact(1);
    }

    @Test
    void testGetContactById_NotFound() throws Exception {
        when(contactService.getContactById(99)).thenThrow(new ContactNotFoundException("Contact not found"));

        mockMvc.perform(get("/api/contacts/get/99"))  // Ensure the correct endpoint
                .andExpect(status().isNotFound())      // Expect 404
                .andExpect(content().string(containsString("Contact not found")));
    }


}
