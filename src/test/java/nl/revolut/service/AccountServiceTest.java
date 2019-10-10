package nl.revolut.service;


import nl.revolut.exception.AccountBalanceException;
import nl.revolut.exception.AccountVerificationException;
import nl.revolut.model.Account;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AccountServiceTest {

    public static final String ACCOUNT_1 = "acc1";
    public static final String ACCOUNT_2 = "acc2";
    private final Account firstAccount = new Account(ACCOUNT_1, BigDecimal.TEN);
    private final Account secondAccount = new Account(ACCOUNT_2, BigDecimal.valueOf(100));
    private AccountService service;

    @Before
    public void setUp(){
        HashMap<String, Account>accountMap = new HashMap();
        accountMap.put(firstAccount.getAccountId(),firstAccount);
        accountMap.put(secondAccount.getAccountId(),secondAccount);
        service = new AccountService(accountMap);
    }

    @Test
    public void makeTransactionNotEnoughFunds() throws AccountVerificationException {
        try{
            service.makeTransaction(firstAccount.getAccountId(),secondAccount.getAccountId(),BigDecimal.valueOf(11));
            fail();
        } catch (AccountBalanceException e){
            assertTrue(service.getAccountById(ACCOUNT_1).get().getBalance().compareTo(BigDecimal.TEN)==0);
        }
    }

    @Test(expected = AccountVerificationException.class)
    public void makeTransactionWrongCreditAccount() throws AccountVerificationException {
        service.makeTransaction("WRONG",secondAccount.getAccountId(),BigDecimal.valueOf(11));
    }

    @Test(expected = AccountVerificationException.class)
    public void makeTransactionWrongDebitAccount() throws AccountVerificationException {
        service.makeTransaction(firstAccount.getAccountId(),"WRONG",BigDecimal.valueOf(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void makeTransactionNegativeAmount() throws AccountVerificationException {
        service.makeTransaction(firstAccount.getAccountId(),secondAccount.getAccountId(),BigDecimal.valueOf(-1));
    }

    @Test
    public void makeTransactionSuccessful() throws AccountVerificationException {
        service.makeTransaction(firstAccount.getAccountId(),secondAccount.getAccountId(),BigDecimal.valueOf(10));
        assertEquals(BigDecimal.ZERO,service.getAccountById(ACCOUNT_1).get().getBalance());
        assertEquals(BigDecimal.valueOf(110),service.getAccountById(ACCOUNT_2).get().getBalance());

    }

    @Test
    public void fetchAll() {
        service = new AccountService(new HashMap());
        assertTrue(service.fetchAll().isEmpty());
        service.create(firstAccount);
        List<Account>accountList=service.fetchAll();
        Assertions.assertThat(accountList.size()).isEqualTo(1);
        Assertions.assertThat(accountList).containsExactlyInAnyOrder(firstAccount);
        service.create(secondAccount);
        accountList=service.fetchAll();
        Assertions.assertThat(accountList.size()).isEqualTo(2);
        Assertions.assertThat(accountList).containsExactlyInAnyOrder(firstAccount,secondAccount);
    }

    @Test
    public void getAccountById() {
        Optional<Account> accountOpt = service.getAccountById(ACCOUNT_1);
        assertTrue(accountOpt.isPresent());
        Assertions.assertThat(accountOpt.get()).isEqualTo(firstAccount);
        assertFalse(service.getAccountById("WRONG").isPresent());
    }

    @Test
    public void updateSuccessful() {
        BigDecimal expectedBalance = BigDecimal.valueOf(888);
        service.updateBalance(ACCOUNT_1, expectedBalance);
        assertEquals(expectedBalance, service.getAccountById(ACCOUNT_1).get().getBalance());
    }

    @Test
    public void updateNonExistent() {
        BigDecimal expectedBalance = BigDecimal.valueOf(-888);
        assertNull(service.updateBalance("WRONG", expectedBalance));
    }

    @Test
    public void delete() {
        assertTrue(service.delete(ACCOUNT_1));
        assertFalse(service.getAccountById(ACCOUNT_1).isPresent());
        assertFalse(service.delete(ACCOUNT_1));
    }
}