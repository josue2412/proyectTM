package com.example.josuerey.helloworld.application.vehicularcap;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CounterStats {
    private AtomicInteger totalCount;
    private AtomicInteger partialCount;
    private static final int MINIMUM_VALUE = 0;

    public int increment() {
        this.partialCount.incrementAndGet();
        return this.totalCount.incrementAndGet();
    }

    public int decrement() {

        if (this.partialCount.get() > MINIMUM_VALUE) {
            this.partialCount.decrementAndGet();
        }
        if (this.totalCount.get() > MINIMUM_VALUE) {
            this.totalCount.decrementAndGet();
        }
        return this.totalCount.get();
    }

    public int flushPartialCount() {
        return this.partialCount.getAndSet(MINIMUM_VALUE);
    }
}
