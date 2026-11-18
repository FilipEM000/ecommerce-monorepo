package com.ecommerce.order.dto.invoice;

public record SellerDto(
        String companyName,
        String taxId,
        String address,
        String city,
        String postalCode
) {
    public static SellerDto defaultSeller() {
        return new SellerDto(
                "E-Commerce",
                "PL1234567890",
                "ul. Nowogrodzka 10",
                "Warszawa",
                "00-001"
        );
    }
}
