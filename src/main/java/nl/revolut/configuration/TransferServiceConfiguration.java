package nl.revolut.configuration;

import io.dropwizard.Configuration;
import lombok.Data;
import nl.revolut.model.Account;

import java.util.Map;


@Data
public class TransferServiceConfiguration extends Configuration {
    private int processingThreadPoolSize;
    private int shutdownDelayMills;
    private Map<String, Account> accountMap;
}
