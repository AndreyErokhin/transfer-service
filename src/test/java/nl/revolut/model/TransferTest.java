package nl.revolut.model;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class TransferTest {

    @Test(expected = IllegalArgumentException.class)
    public void zeroAmount() {
        new Transfer("1", "1", "2", BigDecimal.ZERO, ProcessingResult.NOT_PROCESSED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeAmount() {
        new Transfer("1", "1", "2", BigDecimal.ONE.negate(), ProcessingResult.NOT_PROCESSED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void selfTransfer() {
        new Transfer("1", "1", "1", BigDecimal.ONE, ProcessingResult.NOT_PROCESSED);
    }
}