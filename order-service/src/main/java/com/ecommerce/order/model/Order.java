package com.ecommerce.order.model;

import com.ecommerce.order.exception.InvalidOrderStatusTransitionException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private String paymentMethod;
    private String deliveryMethod;
    @Embedded
    private CustomerDetails customerDetails;
    private BigDecimal totalAmount;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items;

    public void changeStatus(OrderStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new InvalidOrderStatusTransitionException("Cannot change order status from " + this.status + " to " + newStatus);
        }
        this.status = newStatus;
    }
}
