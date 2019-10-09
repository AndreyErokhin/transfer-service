package nl.revolut.model;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class TransferRequest {
    private final String creditAccountId;
    private final String debitAccountId;
    private final  BigDecimal amount;
}
