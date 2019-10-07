package nl.revolut.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    private final String creditAccountId;
    private final String debitAccountId;
    private final  BigDecimal amount;
}
