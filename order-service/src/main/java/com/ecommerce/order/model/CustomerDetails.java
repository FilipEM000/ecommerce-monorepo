package com.ecommerce.order.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetails {
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private String phoneNumber;
}
