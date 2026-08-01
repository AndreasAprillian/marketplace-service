package org.acme.shared.util;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public final class OrderIdGenerator {

    private static final AtomicLong SEQUENCE = new AtomicLong(
            ThreadLocalRandom.current().nextInt(0, 90000));

    private OrderIdGenerator() {
    }

    public static String generateOrderId() {
        return String.format("ORD-%05d", SEQUENCE.getAndIncrement() % 100000);
    }
}
