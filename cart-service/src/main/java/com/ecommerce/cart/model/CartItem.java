package com.ecommerce.cart.model;

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
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "cart_id", insertable = false, updatable = false)
    private Long cartId;
    private Long productId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
    @ElementCollection
    private List<SelectedConfiguration> configurations;

    public BigDecimal getTotalItemPrice() {
        BigDecimal extraPrices = configurations.stream()
                .map(SelectedConfiguration::getAdditionalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return unitPrice.add(extraPrices).multiply(BigDecimal.valueOf(quantity));
    }

    public boolean hasSameProductAndConfigurations(Long incomingProductId, List<SelectedConfiguration> incomingConfigurations) {
        if (!this.productId.equals(incomingProductId)) {
            return false;
        }

        List<SelectedConfiguration> currentConfigs = (this.configurations != null) ? this.configurations : List.of();
        List<SelectedConfiguration> targetConfigs = (incomingConfigurations != null) ? incomingConfigurations : List.of();

        return currentConfigs.size() == targetConfigs.size()
                && currentConfigs.containsAll(targetConfigs);
    }

    public void increaseQuantity(Integer amount) {
        this.quantity += amount;
    }
}
