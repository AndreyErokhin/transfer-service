package nl.revolut.service;

import nl.revolut.model.Account;
import nl.revolut.model.ProcessingResult;
import nl.revolut.model.Transfer;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TransferServiceTest {
    private final String ACCOUNT_1 = "acc1";
    private final String ACCOUNT_2 = "acc2";
    private final Account firstAccount = new Account(ACCOUNT_1, BigDecimal.TEN);
    private final Account secondAccount = new Account(ACCOUNT_2, BigDecimal.valueOf(100));
    private TransferService service;

    @Before
    public void setUp() {
        HashMap<String, Account> accountMap = new HashMap();
        accountMap.put(firstAccount.getAccountId(), firstAccount);
        accountMap.put(secondAccount.getAccountId(), secondAccount);
        AccountService accountService = new AccountService(new HashMap<>());
        ProcessingService processingService = new ProcessingService(1000, 1, accountService);
        service = new TransferService(processingService);
    }

    @Test
    public void submitAnyTransfer() {
        Transfer submitted = service.submitTransfer(ACCOUNT_1, ACCOUNT_2, BigDecimal.TEN);
        assertEquals(ProcessingResult.NOT_PROCESSED, submitted.getProcessingResult());
    }

    @Test
    public void findTransferById() {
        Transfer submitted = service.submitTransfer(ACCOUNT_1, ACCOUNT_2, BigDecimal.TEN);
        assertTrue(service.getTransferById(submitted.getTransferId()).isPresent());
        assertFalse(service.getTransferById("WRONG").isPresent());
    }
}