package nl.revolut;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import nl.revolut.configuration.TransferServiceConfiguration;
import nl.revolut.model.Account;
import nl.revolut.model.Transfer;
import nl.revolut.model.TransferRequest;
import org.assertj.core.api.Assertions;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TransferServiceApplicationTest {

    @ClassRule
    public static final DropwizardAppRule<TransferServiceConfiguration> RULE =
        new DropwizardAppRule<TransferServiceConfiguration>(TransferServiceApplication.class, ResourceHelpers.resourceFilePath("config.yml"));

    @Test(timeout = 1000)
    public void submitTransferAndCheckBalance() throws InterruptedException {
        Client client = new JerseyClientBuilder().build();

        Response response = client.target(
            String.format("http://localhost:%d/accounts", RULE.getLocalPort()))
            .request()
            .get();

        Assertions.assertThat(response.getStatus()).isEqualTo(200);

        List<Account> accounts = client.target(
            String.format("http://localhost:%d/accounts", RULE.getLocalPort()))
            .request()
            .get(new ArrayList<Account>().getClass());

        Assertions.assertThat(accounts.size()).isEqualTo(2);

        Account creditAccountBeforeTransfer = client.target(
            String.format("http://localhost:%d/accounts/%s", RULE.getLocalPort(),"revolut1"))
            .request()
            .get(Account.class);
        Account debitAccountBeforeTransfer = client.target(
            String.format("http://localhost:%d/accounts/%s", RULE.getLocalPort(),"revolut2"))
            .request()
            .get(Account.class);
        BigDecimal transferAmount = BigDecimal.TEN;

        TransferRequest valid = new TransferRequest("revolut1", "revolut2", transferAmount);
        Transfer transfer = client.target(
            String.format("http://localhost:%d/transfers", RULE.getLocalPort()))
            .request()
            .post(Entity.json(valid), Transfer.class);

        TimeUnit.MILLISECONDS.sleep(500);


        Account creditAccountAfterTransfer = client.target(
            String.format("http://localhost:%d/accounts/%s", RULE.getLocalPort(),"revolut1"))
            .request()
            .get(Account.class);

        Account debitAccountAfterTransfer = client.target(
            String.format("http://localhost:%d/accounts/%s", RULE.getLocalPort(),"revolut2"))
            .request()
            .get(Account.class);
        Assertions.assertThat(creditAccountAfterTransfer.getBalance()).isEqualTo(creditAccountBeforeTransfer.getBalance().subtract(transferAmount));
        Assertions.assertThat(debitAccountAfterTransfer.getBalance()).isEqualTo(debitAccountBeforeTransfer.getBalance().add(transferAmount));

    }
}