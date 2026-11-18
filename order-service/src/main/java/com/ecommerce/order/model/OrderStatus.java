package com.ecommerce.order.model;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public enum OrderStatus {
    CREATED, PAID, PROCESSING, COMPLETED, CANCELLED;

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            CREATED, Set.of(PAID, CANCELLED),
            PAID, Set.of(PROCESSING, CANCELLED),
            PROCESSING, Set.of(COMPLETED, CANCELLED),
            COMPLETED, Collections.emptySet(),
            CANCELLED, Collections.emptySet()
    );

    public boolean canTransitionTo(OrderStatus newStatus) {
        if (newStatus == null || newStatus == this) {
            return false;
        }
        return ALLOWED_TRANSITIONS.getOrDefault(this, Collections.emptySet()).contains(newStatus);
    }
}
