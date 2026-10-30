package com.ecommerce.product.model;

import com.ecommerce.product.command.UpdateProductCommand;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private ProductType productType;
    private Integer totalQuantity;
    private BigDecimal basePrice;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private List<ProductConfiguration> configurations;

    public void update(UpdateProductCommand updateProductCommand, List<ProductConfiguration> newConfigurations) {
        this.name = updateProductCommand.name();
        this.description = updateProductCommand.description();
        this.productType = updateProductCommand.productType();
        this.basePrice = updateProductCommand.basePrice();
        this.totalQuantity = updateProductCommand.totalQuantity();
        if (this.configurations == null) {
            this.configurations = new ArrayList<>();
        }
        this.configurations.clear();
        if (newConfigurations != null) {
            this.configurations.addAll(newConfigurations);
        }
    }
}
