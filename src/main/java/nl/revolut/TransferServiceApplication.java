package nl.revolut;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import nl.revolut.api.AccountApi;
import nl.revolut.api.TransferApi;
import nl.revolut.api.TransferServiceHealthCheck;
import nl.revolut.configuration.BinderConfiguration;
import nl.revolut.configuration.TransferServiceConfiguration;

public class TransferServiceApplication extends Application<TransferServiceConfiguration> {

    public static void main(final String[] args) throws Exception {
        new TransferServiceApplication().run(args);
    }

    @Override
    public void run(
        final TransferServiceConfiguration configuration,
        final Environment environment
    ) {
        environment.jersey().register(new BinderConfiguration(configuration));
        environment.jersey().register(TransferApi.class);
        environment.jersey().register(AccountApi.class);
        environment.healthChecks().register(
            "template",
            new TransferServiceHealthCheck(configuration.getProcessingThreadPoolSize())
        );

    }


}
