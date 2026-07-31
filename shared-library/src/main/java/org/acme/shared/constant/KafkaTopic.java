package org.acme.shared.constant;

public final class KafkaTopic {
    public static final String ORDER_CREATED = "order-created";
    public static final String ORDER_VALIDATED = "order-validated";
    public static final String ORDER_PROCESSED = "order-processed";
    public static final String PAYMENT_PROCESSED = "payment-processed";
    public static final String ORDER_FAILED = "order-failed";
    public static final String CUSTOMER_REGISTERED = "customer-registered";
    public static final String ORDER_NOTIFICATION = "order-notification";

    private KafkaTopic() {}
}
