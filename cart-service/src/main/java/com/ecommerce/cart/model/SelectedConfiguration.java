package com.ecommerce.cart.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SelectedConfiguration {
    @NotBlank
    private String category;
    @NotBlank
    private String value;
    @NotNull
    @PositiveOrZero
    private BigDecimal additionalPrice;
}
