package nl.revolut.exception;

public class AccountVerificationException extends Exception {
    public AccountVerificationException(final String errorMessage) {
        super(errorMessage);
    }
}
