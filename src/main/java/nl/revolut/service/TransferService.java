package nl.revolut.service;


import nl.revolut.model.ProcessingResult;
import nl.revolut.model.Transfer;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TransferService {

    private final ConcurrentHashMap<String, Transfer> transferMap = new ConcurrentHashMap();
    private final ProcessingService processingService;

    @Inject
    public TransferService(final ProcessingService processingService) {
        this.processingService = processingService;
    }

    public Transfer submitTransfer(final String creditAccountId, final String debitAccountId, final BigDecimal amount) {
        Transfer newTransfer = createNewTransfer(creditAccountId, debitAccountId, amount);
        processingService.startProcessing(newTransfer)
            .thenApply(success -> updateProcessingResult(
                newTransfer.getTransferId(),
                success ? ProcessingResult.SUCCESSFUL : ProcessingResult.FAILED
            ));
        return newTransfer;
    }

    public Optional<Transfer> getTransferById(final String transferId) {
        Transfer transfer = transferMap.get(transferId);
        return transfer != null ? Optional.of(transfer) : Optional.empty();
    }

    private Transfer createNewTransfer(
        final String creditAccountId,
        final String debitAccountId,
        final BigDecimal amount
    ) {
        String newTransferId = generateRandomTransferId();
        //double check that transfer with such an id isn't exist in the storage.
        Transfer newTransfer = transferMap.computeIfAbsent(
            newTransferId,
            transferId -> !transferMap.contains(transferId) ? new Transfer(
                transferId,
                creditAccountId,
                debitAccountId,
                amount,
                ProcessingResult.NOT_PROCESSED
            ) : null
        );
        // if transfer exist, we should try to create it one more time because id generation can possibly produce duplicates.
        return newTransfer != null ? newTransfer : createNewTransfer(creditAccountId, debitAccountId, amount);
    }

    private Transfer updateProcessingResult(final String transferId, final ProcessingResult processingResult) {
        Transfer newTransfer = transferMap.computeIfPresent(
            transferId,
            (oldTransferId, oldTransfer) -> oldTransfer.setProcessingResult(
                processingResult)
        );
        //TODO: needs to be tested
        if (newTransfer == null) {
            throw new IllegalArgumentException(String.format("Transfer with id=%s doesn't exist.", transferId));
        }
        return newTransfer;
    }

    private String generateRandomTransferId() {
        String transferId = UUID.randomUUID().toString();
        //if generated id exist, retry to generate unique one.
        return !this.getTransferById(transferId).isPresent() ? transferId : generateRandomTransferId();
    }

}
