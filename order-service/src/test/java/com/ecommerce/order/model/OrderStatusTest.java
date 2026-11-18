package com.ecommerce.order.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class OrderStatusTest {

    @ParameterizedTest
    @CsvSource({
            "CREATED, PAID, true",
            "CREATED, CANCELLED, true",
            "CREATED, COMPLETED, false",
            "PAID, PROCESSING, true",
            "PAID, CANCELLED, true",
            "PAID, CREATED, false",
            "COMPLETED, CANCELLED, false",
            "CANCELLED, PAID, false"
    })
    void canTransitionTo_variousStatuses_correctBooleanReturned(OrderStatus current, OrderStatus next, boolean expected) {
        Assertions.assertEquals(expected, current.canTransitionTo(next));
    }

    @Test
    void canTransitionTo_nullOrSameStatus_returnsFalse() {
        Assertions.assertFalse(OrderStatus.CREATED.canTransitionTo(null));
        Assertions.assertFalse(OrderStatus.CREATED.canTransitionTo(OrderStatus.CREATED));
    }
}
