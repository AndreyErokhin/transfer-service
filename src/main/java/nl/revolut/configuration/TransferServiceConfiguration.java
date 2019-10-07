package nl.revolut.configuration;

import io.dropwizard.Configuration;
import lombok.Data;
import nl.revolut.model.Account;

import java.util.concurrent.ConcurrentHashMap;


@Data
public class TransferServiceConfiguration extends Configuration {
    private int processingThreadPoolSize;
    private int shutdownDelayMills;
    private ConcurrentHashMap<String, Account> accountMap;
}
