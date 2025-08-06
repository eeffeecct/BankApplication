package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Payment(Long id,
                      Long fromAccountId,
                      Long toAccountId,
                      BigDecimal amount,
                      String currency,
                      LocalDateTime transactionDate,
                      String transactionType,
                      String status,
                      String description) {
}