package nl.revolut.model;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class Transfer {
    private final String transferId;
    private final String debitAccountId;
    private final String creditAccountId;
    private final BigDecimal amount;
    private final ProcessingResult processingResult;
    private final long timestamp;

    /**
     * Creates new Transfer object.
     * @param transferId
     * @param creditAccountId
     * @param debitAccountId
     * @param amount
     * @param processingResult
     * @throws IllegalArgumentException if the amount is less or equal to 0;
     */
    public Transfer(
        final String transferId,
        final String creditAccountId,
        final String debitAccountId,
        final BigDecimal amount,
        final ProcessingResult processingResult
    ) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        this.transferId = transferId;
        this.debitAccountId = debitAccountId;
        this.creditAccountId = creditAccountId;
        this.amount = amount;
        this.processingResult = processingResult;
        timestamp = System.currentTimeMillis();
    }

    public Transfer setProcessingResult(final ProcessingResult updatedProcessingResult) {
        return new Transfer(
            this.getTransferId(),
            this.getCreditAccountId(),
            this.getDebitAccountId(),
            this.getAmount(),
            updatedProcessingResult
        );
    }
}
