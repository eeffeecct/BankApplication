package org.example.model;

import java.math.BigDecimal;

public record Order(Long id,
                    Long clientId,
                    BigDecimal amount,
                    String status) {
}
