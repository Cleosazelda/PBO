package com.rental.rentalapp.model.enums;

public enum PaymentMethod {
    BANK_MANDIRI("Transfer Bank Mandiri"),
    BANK_BCA("Transfer Bank BCA"),
    QRIS("QRIS");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}