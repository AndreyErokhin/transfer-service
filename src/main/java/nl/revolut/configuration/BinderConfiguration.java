package nl.revolut.configuration;

import nl.revolut.model.Account;
import nl.revolut.service.AccountService;
import nl.revolut.service.ProcessingService;
import nl.revolut.service.TransferService;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;
import java.util.Map;

public class BinderConfiguration extends AbstractBinder {
    private final TransferServiceConfiguration configuration;

    public BinderConfiguration(final TransferServiceConfiguration configuration) {
        this.configuration = configuration;
    }


    @Override
    protected void configure() {
        bind(TransferService.class).to(TransferService.class).in(Singleton.class);
        bind(configuration.getShutdownDelayMills()).to(Integer.class).named("SHUTDOWN_DELAY_MILLS");
        bind(configuration.getProcessingThreadPoolSize()).to(Integer.class).named("THREAD_POOL_SIZE");
        bind(AccountService.class).to(AccountService.class).in(Singleton.class);
        bind(TransferService.class).to(TransferService.class).in(Singleton.class);
        bind(ProcessingService.class).to(ProcessingService.class).in(Singleton.class);
        bind(configuration.getAccountMap()).to(new TypeLiteral<Map<String, Account>>() {
        }).named("INITIAL_ACCOUNT_MAP");
    }

}
