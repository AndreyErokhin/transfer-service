package nl.revolut.api;

import nl.revolut.model.Account;
import nl.revolut.service.AccountService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Path("/accounts")
public class AccountApi {

    @Inject
    private AccountService accountService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Account> fetchAll() {
        return accountService.fetchAll();
    }

    @GET
    @Path("account/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("id") final String accountId) {
        Optional<Account> accountOpt = accountService.getAccountById(accountId);
        return accountOpt.isPresent()
               ? Response.ok().entity(accountOpt.get()).build()
               : notFound(accountId);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(final Account account) {
        //FIXME: content validation, think of error codes
        accountService.create(account);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/account/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateBalance(@PathParam("id") final String accountId, final BigDecimal newBalance) {
        Account updatedAccount = accountService.updateBalance(accountId, newBalance);
        return updatedAccount != null ? Response.ok().entity(updatedAccount).build() : notFound(accountId);
    }

    @DELETE
    @Path("/account/{id}")
    public Response delete(@PathParam("id") final String accountId) {
        boolean deleted = accountService.delete(accountId);
        return deleted
               ? Response.status(Response.Status.NO_CONTENT).entity("Account deleted successfully.").build()
               : notFound(accountId);
    }

    private Response notFound(final String accountId) {
        return Response.status(Response.Status.NOT_FOUND)
            .entity(String.format("Account with id %s not found", accountId))
            .build();
    }
}
