package org.example.model;

import java.util.Date;

public record Client(Long id,
                     String firstName,
                     String lastName,
                     Date birthDate,
                     String passportNumber,
                     String phoneNumber,
                     String email,
                     String status) {
}
