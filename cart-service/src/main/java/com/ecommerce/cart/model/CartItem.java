package com.ecommerce.cart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "cart_id", insertable = false, updatable = false)
    private Long cartId;
    private Long productId;
    private Integer quantity;
    @ElementCollection
    private List<String> selectedConfigurations;
}
