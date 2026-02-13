package com.tarrina.orders.controller;

import com.tarrina.orders.dto.ContactResponse;
import com.tarrina.orders.dto.CreateContactDto;
import com.tarrina.orders.entity.Contact;
import com.tarrina.orders.repository.ContactRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {
        private final ContactRepository contactRepository;

        public ContactController(ContactRepository contactRepository) {
            this.contactRepository = contactRepository;
        }

        // POST /api/v1/contacts
        @PostMapping
        public ContactResponse create(@Valid @RequestBody CreateContactDto req) {
            Contact contact = new Contact();
            contact.setName(req.getName());
            contact.setPhone(req.getPhone());
            contact.setEmail(req.getEmail());
            return new ContactResponse(contactRepository.save(contact));
        }

        // GET /api/v1/contacts/{id}
        @GetMapping("/{id}")
        public ContactResponse get(@PathVariable Long id) {
            Contact contact = contactRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Contact not found"));

            return new ContactResponse(contact);
        }
    }
