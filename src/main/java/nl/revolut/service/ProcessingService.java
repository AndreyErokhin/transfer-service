package nl.revolut.service;

import lombok.extern.slf4j.Slf4j;
import nl.revolut.exception.AccountVerificationException;
import nl.revolut.model.Transfer;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ProcessingService {
    private final int shutdownDelay;
    private final ExecutorService executorService;
    private final AccountService accountService;

    @Inject
    public ProcessingService(
        @Named("SHUTDOWN_DELAY_MILLS") final int shutdownDelay,
        @Named("THREAD_POOL_SIZE")final int nThreads,
        final AccountService accountService
    ) {
        this.shutdownDelay = shutdownDelay;
        executorService = Executors.newFixedThreadPool(nThreads);
        this.accountService = accountService;
    }

    public CompletableFuture<Boolean> startProcessing(final Transfer transfer) {
        CompletableFuture<Boolean> transferTask = CompletableFuture.supplyAsync(() -> {
            try {
                return accountService.makeTransaction(
                    transfer.getCreditAccountId(),
                    transfer.getDebitAccountId(),
                    transfer.getAmount()
                );
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
                return false;
            }
        }, executorService);
        return transferTask;
    }


    @PreDestroy
    public void finish() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(shutdownDelay, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
