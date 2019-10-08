package nl.revolut.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Account {
    private String accountId;
    private BigDecimal balance;

    public boolean credit(final BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Credit amount should be possitive.");
        }
        if (balance.compareTo(amount) < 0) {
            return false;
        }
        balance = balance.subtract(amount);
        return true;
    }

    public boolean debit(final BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debit amount should be positive.");
        }
        balance = balance.add(amount);
        return true;
    }
}
