package com.ecommerce.order.model;

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
@Table(name = "order_items")
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue
    private Long id;
    private Long productId;
    @Column(name = "order_id", insertable = false, updatable = false)
    private Long orderId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
    @ElementCollection
    private List<String> selectedConfigurations;
}
