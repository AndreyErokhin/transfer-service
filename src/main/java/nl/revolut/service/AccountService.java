package nl.revolut.service;

import nl.revolut.exception.AccountBalanceException;
import nl.revolut.exception.AccountVerificationException;
import nl.revolut.model.Account;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AccountService {
    private final ConcurrentHashMap<String, Account> accountMap;

    @Inject
    public AccountService(@Named("INITIAL_ACCOUNT_MAP") final Map<String, Account> accountMap) {
        this.accountMap = new ConcurrentHashMap(accountMap);
    }

    /**
     * @param creditAccountId
     * @param debitAccountId
     * @param amount
     * @return
     * @throws AccountVerificationException,AccountBalanceException If account not fount or the balance isn't sufficient to execute the transfer.
     */
    public boolean makeTransaction(final String creditAccountId, final String debitAccountId, final BigDecimal amount)
        throws AccountVerificationException {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Transfer amount must be a positive number!");
        }
        creditAccount(creditAccountId, amount);
        try {
            debitAccount(debitAccountId, amount);
        } catch (AccountVerificationException e) {
            //debit account not found return funds.
            rollBackTransfer(creditAccountId, amount);
            throw e;
        }
        return true;
    }

    private void rollBackTransfer(final String creditAccountId, final BigDecimal amount) {
        try {
            debitAccount(creditAccountId, amount);
        } catch (AccountVerificationException disaster) {
            //can't return money back. We can pay bonus to the developers now :)
            //probably there should be some notification, and separate procedure how to handle such a situations.
            throw new RuntimeException(disaster);
        }
    }

    private void creditAccount(final String creditAccountId, final BigDecimal amount)
        throws AccountVerificationException, AccountBalanceException {
        Account updatedAccout = accountMap.computeIfPresent(creditAccountId, (accountId, creditAccount) -> {
            //not enough funds
            if (creditAccount.getBalance().compareTo(amount) < 0) {
                throw new AccountBalanceException(
                    String.format("Can't charge the accountId=%s. Insufficient funds.", accountId)
                );
            }
            BigDecimal newBalance = creditAccount.getBalance().subtract(amount);
            return new Account(accountId, newBalance);
        });
        if (updatedAccout == null) {
            throw new AccountVerificationException(String.format("Credit account %s not found.", creditAccountId));
        }
    }

    private void debitAccount(final String debitAccountId, final BigDecimal amount)
        throws AccountVerificationException {
        Account updatedAccout = accountMap.computeIfPresent(debitAccountId, (accountId, debitAccount) -> {
            BigDecimal newBalance = debitAccount.getBalance().add(amount);
            return new Account(accountId, newBalance);
        });
        if (updatedAccout == null) {
            throw new AccountVerificationException(String.format("Debit account %s not found.", debitAccountId));
        }
    }

    private Account resolveAccount(final String accountId) throws AccountVerificationException {
        Account creditAccount = accountMap.get(accountId);
        if (creditAccount == null) {
            throw new AccountVerificationException(String.format("Account %s not found.", accountId));
        }
        return creditAccount;
    }

    public List<Account> fetchAll() {
        return new ArrayList<>(accountMap.values());
    }

    public Optional<Account> getAccountById(final String accountId) {
        Account account = accountMap.get(accountId);
        return account != null ? Optional.of(account) : Optional.empty();
    }

    /**
     * @param account
     * @return created account or null if account with the same id is already present.
     */
    public Account create(final Account account) {
        return accountMap.computeIfAbsent(
            account.getAccountId(),
            accountId -> !accountMap.contains(accountId) ? account : null
        );
    }


    //TODO: is negative balance check required? Not sure.
    public Account updateBalance(final String accountId, final BigDecimal newBalance) {
        return accountMap.computeIfPresent(
            accountId,
            (oldAccountId, oldAccount) -> new Account(oldAccountId, newBalance)
        );
    }

    public boolean delete(final String accountId) {
        return accountMap.remove(accountId) != null;
    }
}
