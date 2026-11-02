package com.ecommerce.order.dto;

public record CustomerDetailsDto(
        String firstName,
        String lastName,
        String address,
        String email,
        String phoneNumber
) {
}
