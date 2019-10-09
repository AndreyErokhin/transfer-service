package nl.revolut.model;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Account {
    private final String accountId;
    private final BigDecimal balance;
}
