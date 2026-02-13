package com.tarrina.orders.dto;

import com.tarrina.orders.entity.Contact;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactResponse {

        private Long id;
        private String name;
        private String phone;
        private String email;
        public ContactResponse(Contact contact) {
            this.id = contact.getId();
            this.name = contact.getName();
            this.phone = contact.getPhone();
            this.email = contact.getEmail();
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }
    }
