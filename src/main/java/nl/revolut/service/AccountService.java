package nl.revolut.service;

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

    public boolean makeTransaction(final String creditAccountId, final String debitAccountId, final BigDecimal amount)
        throws AccountVerificationException {
        //FIXME: race condition. must be checked for thread safety
        Account creditAccount = resolveAccount(creditAccountId);
        Account debitAccount = resolveAccount(debitAccountId);
        //get money from the credit account
        if (creditAccount.credit(amount)) {
            //add money to the debit account
            if (debitAccount.debit(amount)) {
                //debit and credit both succeed
                return true;
            } else {
                //only credit succeed, rollback transaction
                creditAccount.debit(amount);
                return false;
            }
        } else {
            //credit failed(so far it can happen only because on insufficient founds, overdraft isn't allowed)
            return false;
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

    /**
     * @param updatedAccount
     * @return updated account or null if account with the same id is not found.
     */
    public Account update(final Account updatedAccount) {
        return accountMap.computeIfPresent(updatedAccount.getAccountId(), (accountId, oldAccount) -> updatedAccount);
    }

    public boolean delete(final String accountId) {
        return accountMap.remove(accountId) != null;
    }
}
