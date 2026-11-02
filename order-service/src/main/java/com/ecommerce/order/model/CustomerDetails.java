package com.ecommerce.order.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private String firstName;
    private String lastName;
    private String address;
    @NotBlank
    @Email
    private String email;
    private String phoneNumber;
}
