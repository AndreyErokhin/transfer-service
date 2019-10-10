package nl.revolut.api;

import nl.revolut.model.Transfer;
import nl.revolut.model.TransferRequest;
import nl.revolut.service.TransferService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/transfers")
@Produces(MediaType.APPLICATION_JSON)
public class TransferApi {
    private final TransferService transferService;

    @Inject
    public TransferApi(final TransferService transferService) {
        this.transferService = transferService;
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response executeTransfer(final TransferRequest transferRequest)
        throws URISyntaxException {
        try {
            Transfer transfer = transferService.submitTransfer(
                transferRequest.getCreditAccountId(),
                transferRequest.getDebitAccountId(),
                transferRequest.getAmount()
            );
            return Response.created(new URI("/transfers/" + transfer.getTransferId()))
                .entity(transfer)
                .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(e.getMessage())
                .build();
        }
    }

    @GET
    @Path("/{transferId}")
    @Produces(APPLICATION_JSON)
    public Response getTransfer(@PathParam("transferId")final String transferId) {
        return transferService.getTransferById(transferId)
            .map(transfer -> Response.ok().entity(transfer))
            .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity(String.format("Transfer:  with id %s not found", transferId)))
            .build();
    }

    @GET
    @Produces(APPLICATION_JSON)
    public List<Transfer> listAllTransfers() {
        return transferService.fetchAllTransfers();
    }
}
