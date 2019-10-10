package nl.revolut.service;

import nl.revolut.model.Account;
import nl.revolut.model.ProcessingResult;
import nl.revolut.model.Transfer;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProcessingServiceTest {
    private final String ACCOUNT_1 = "acc1";
    private final String ACCOUNT_2 = "acc2";
    private final Account firstAccount = new Account(ACCOUNT_1, BigDecimal.TEN);
    private final Account secondAccount = new Account(ACCOUNT_2, BigDecimal.valueOf(100));
    private ProcessingService service;

    @Before
    public void setUp() {
        HashMap<String, Account> accountMap = new HashMap();
        accountMap.put(firstAccount.getAccountId(), firstAccount);
        accountMap.put(secondAccount.getAccountId(), secondAccount);
        AccountService accountService = new AccountService(accountMap);
        service = new ProcessingService(1000, 1, accountService);
    }

    @Test
    public void processTransfer() throws ExecutionException, InterruptedException {
        Transfer firstToSecondSuccess = new Transfer("1", ACCOUNT_1, ACCOUNT_2, BigDecimal.TEN, ProcessingResult.NOT_PROCESSED);
        assertTrue(service.startProcessing(firstToSecondSuccess).get());
        Transfer firstToSecondFail = new Transfer("1", ACCOUNT_1, ACCOUNT_2, BigDecimal.TEN, ProcessingResult.NOT_PROCESSED);
        assertFalse(service.startProcessing(firstToSecondFail).isCompletedExceptionally());
        Transfer wrongCredit = new Transfer("1", "WRONG", ACCOUNT_2, BigDecimal.TEN, ProcessingResult.NOT_PROCESSED);
        assertFalse(service.startProcessing(wrongCredit).isCompletedExceptionally());
        Transfer wrongDebit = new Transfer("1", ACCOUNT_1, "WRONG", BigDecimal.TEN, ProcessingResult.NOT_PROCESSED);
        assertFalse(service.startProcessing(wrongDebit).isCompletedExceptionally());
    }
}