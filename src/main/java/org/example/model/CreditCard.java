package org.example.model;

import java.math.BigDecimal;

public record CreditCard(Long id,
                         Long clientId,
                         Long accountId,
                         String cardNumber,
                         BigDecimal creditLimit,
                         BigDecimal debt,
                         boolean isBlocked) {
}
