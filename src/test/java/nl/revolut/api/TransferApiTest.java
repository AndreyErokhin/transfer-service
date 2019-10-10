package nl.revolut.api;

import io.dropwizard.testing.junit.ResourceTestRule;
import nl.revolut.model.Account;
import nl.revolut.model.Transfer;
import nl.revolut.model.TransferRequest;
import nl.revolut.service.AccountService;
import nl.revolut.service.ProcessingService;
import nl.revolut.service.TransferService;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.HashMap;


public class TransferApiTest {
    private final String ACCOUNT_1 = "acc1";
    private final String ACCOUNT_2 = "acc2";
    private final Account firstAccount = new Account(ACCOUNT_1, BigDecimal.TEN);
    private final Account secondAccount = new Account(ACCOUNT_2, BigDecimal.valueOf(100));

    private TransferService setUpTransferService() {
        HashMap<String, Account> accountMap = new HashMap();
        accountMap.put(firstAccount.getAccountId(), firstAccount);
        accountMap.put(secondAccount.getAccountId(), secondAccount);
        AccountService accountService = new AccountService(accountMap);
        ProcessingService processingService = new ProcessingService(1000, 1, accountService);
        return new TransferService(processingService);
    }

    @Rule
    public ResourceTestRule resources = ResourceTestRule.builder()
        .addResource(new TransferApi(setUpTransferService()))
        .build();



    @Test
    public void executeTransferAsync() {
        TransferRequest valid = new TransferRequest(ACCOUNT_1,ACCOUNT_2,BigDecimal.valueOf(5));
        Assertions.assertThat(resources.target("/transfers").request().post(Entity.json(valid)).getStatus())
            .isEqualTo(Response.Status.CREATED.getStatusCode());

        TransferRequest invalidCredit = new TransferRequest("WRONG",ACCOUNT_2,BigDecimal.valueOf(5));
        Assertions.assertThat(resources.target("/transfers").request().post(Entity.json(invalidCredit)).getStatus())
            .isEqualTo(Response.Status.CREATED.getStatusCode());

        TransferRequest invalidDebit = new TransferRequest(ACCOUNT_1,"WRONG",BigDecimal.valueOf(5));
        Assertions.assertThat(resources.target("/transfers").request().post(Entity.json(invalidDebit)).getStatus())
            .isEqualTo(Response.Status.CREATED.getStatusCode());

        TransferRequest zeroAmount = new TransferRequest(ACCOUNT_1,ACCOUNT_2,BigDecimal.ZERO);
        Assertions.assertThat(resources.target("/transfers").request().post(Entity.json(zeroAmount)).getStatus())
            .isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());

        TransferRequest negativeAmount = new TransferRequest(ACCOUNT_1,ACCOUNT_2,BigDecimal.ONE.negate());
        Assertions.assertThat(resources.target("/transfers").request().post(Entity.json(negativeAmount)).getStatus())
            .isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void getTransfer() {
        TransferRequest valid = new TransferRequest(ACCOUNT_1,ACCOUNT_2,BigDecimal.TEN);
        Transfer submittedTransfer =resources.target("/transfers").request().post(Entity.json(valid), Transfer.class);
        Transfer extractedTransfer = resources.target(String.format("/transfers/%s", submittedTransfer.getTransferId()))
            .request()
            .get(Transfer.class);
        Assertions.assertThat(extractedTransfer.getAmount()).isEqualTo(submittedTransfer.getAmount());
        Assertions.assertThat(extractedTransfer.getCreditAccountId()).isEqualTo(submittedTransfer.getCreditAccountId());
        Assertions.assertThat(extractedTransfer.getDebitAccountId()).isEqualTo(submittedTransfer.getDebitAccountId());
    }

    @Test
    public void getTransferResponseCodeValid(){
        TransferRequest valid = new TransferRequest(ACCOUNT_1,ACCOUNT_2,BigDecimal.TEN);
        Transfer submittedTransfer =resources.target("/transfers").request().post(Entity.json(valid), Transfer.class);
        int responseCode = resources.target(String.format("/transfers/%s", submittedTransfer.getTransferId()))
            .request()
            .get()
            .getStatus();
        Assertions.assertThat(responseCode).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void getTransferResponseCodeInvalid() {
        int status = resources.target(String.format("/transfers/%s", "WRONG")).request().get().getStatus();
        Assertions.assertThat(status).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
    }
}