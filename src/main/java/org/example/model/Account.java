package org.example.model;

import java.math.BigDecimal;

public record Account(Long id,
                      Long clientId,
                      String number,
                      BigDecimal balance,
                      String currency,
                      String status
                      ) {}
