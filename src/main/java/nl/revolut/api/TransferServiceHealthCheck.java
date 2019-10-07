package nl.revolut.api;

import com.codahale.metrics.health.HealthCheck;

public class TransferServiceHealthCheck extends HealthCheck {
    private final int poolSize;

    public TransferServiceHealthCheck(final int poolSize) {
        this.poolSize = poolSize;
    }

    @Override
    protected Result check() throws Exception {
        return Result.healthy("OK. ThreadPoll size: " + this.poolSize);
    }
}
