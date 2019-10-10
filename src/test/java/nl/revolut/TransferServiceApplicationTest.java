package nl.revolut;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import nl.revolut.configuration.TransferServiceConfiguration;
import org.assertj.core.api.Assertions;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

public class TransferServiceApplicationTest {

    @ClassRule
    public static final DropwizardAppRule<TransferServiceConfiguration> RULE =
        new DropwizardAppRule<TransferServiceConfiguration>(TransferServiceApplication.class, ResourceHelpers.resourceFilePath("config.yml"));

    @Test
    public void loginHandlerRedirectsAfterPost() {
        Client client = new JerseyClientBuilder().build();

        Response response = client.target(
            String.format("http://localhost:%d/transfers", RULE.getLocalPort()))
            .request()
            .get();

        Assertions.assertThat(response.getStatus()).isEqualTo(200);
    }
}